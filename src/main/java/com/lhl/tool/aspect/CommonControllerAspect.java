/**
 * Meituan.com Inc.
 * Copyright (c) 2010-2021 All Rights Reserved.
 */
package com.lhl.tool.aspect;

import com.lhl.tool.aop.ExcludeCheckToken;
import com.lhl.tool.errorcode.BaseErrorCodeEnum;
import com.lhl.tool.exception.*;
import com.lhl.tool.model.dto.NotifyDTO;
import com.lhl.tool.response.factory.ResponseFactory;
import com.lhl.tool.util.BaseRequestContextUtils;
import com.lhl.tool.util.NotifyContextUtils;
import com.lhl.tool.util.UserContextDTOUtil;
import com.lhl.tool.validate.BizValidate;
import com.lhl.tool.validate.BizValidatorExecutor;
import com.lhl.tool.validate.StaticValidatorExecutor;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * thrift参数处理切面v2
 * </p>
 *
 * @author liupeiwen02@meituan.com
 * @version CommonThriftAspect.class  2021-01-29 11:47
 * @since
 **/
public class CommonControllerAspect implements BeanDefinitionRegistryPostProcessor, ApplicationListener<ApplicationContextEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonControllerAspect.class);
    private static final String JAVA_PATH_SEPARATOR = ".";
    private static final int EXPECTED_SIZE = 2;
    private static final String PARAMETER_ERROR_BUSINESS_KEY = "请求参数校验不通过";

    private BeanDefinitionRegistry beanDefinitionRegistry;

    protected String inspectPath = "";

    public Object doThriftPointCut(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> clazz = joinPoint.getTarget().getClass();
        Method method = methodSignature.getMethod();
        int index = methodSignature.getDeclaringTypeName().lastIndexOf(JAVA_PATH_SEPARATOR);
        String methodName = methodSignature.getDeclaringTypeName().substring(index + 1)
                + JAVA_PATH_SEPARATOR + methodSignature.getName();
        Class<?> returnType = methodSignature.getReturnType();

        try {
            // 用户维度限频
            doLimit(args, methodName);

            // 用户上下文、请求上下文，供后续各业务使用
            ExcludeCheckToken excludeCheckTokenAnnotation = AnnotationUtils.findAnnotation(method, ExcludeCheckToken.class);
            if (excludeCheckTokenAnnotation == null) {
                checkTokenAndSetContextToThreadLocal(args);
            }

            //利用事务+mybatis缓存优化校验器中重复访问DB场景的性能
            TransactionTemplate transactionTemplate = getTransactionTemplate();
            if (Objects.nonNull(transactionTemplate) && Objects.nonNull(transactionTemplate.getTransactionManager())) {
                PlatformTransactionManager transactionManager = transactionTemplate.getTransactionManager();
                TransactionStatus status = transactionManager.getTransaction(transactionTemplate);
                try {
                    // 校验
                    validate(clazz, method, args);
                    transactionManager.commit(status);
                } catch (Throwable throwable) {
                    transactionManager.rollback(status);
                    throw throwable;
                }
            } else {
                // 校验
                validate(clazz, method, args);
            }

            //如果线程上下文中存在非阻塞校验的数据则组装返回异常
            if (CollectionUtils.isNotEmpty(NotifyContextUtils.getNotifyDTOs())) {
                List<NotifyDTO> notifyDTOs = NotifyContextUtils.getNotifyDTOs();
                return ResponseFactory.fail(BaseErrorCodeEnum.NON_BLOCK_VALIDATOR_ERROR.getCode(), BaseErrorCodeEnum.NON_BLOCK_VALIDATOR_ERROR.getMessage(),
                        returnType, notifyDTOs);
            }

            return joinPoint.proceed();
        } catch (TokenInvalidException e) {
            LOGGER.warn("token validation error, method:{}, parameters:{}", methodName, args, e);
            return ResponseFactory.exception(e, returnType);
        } catch (NonBlockValidateException nonBlockValidateException) {
            List<NotifyDTO> notifyDTOList = nonBlockValidateException.getNotifyDTOList();
            if (CollectionUtils.isNotEmpty(notifyDTOList)) {
                return ResponseFactory.fail(BaseErrorCodeEnum.NON_BLOCK_VALIDATOR_ERROR.getCode(), BaseErrorCodeEnum.NON_BLOCK_VALIDATOR_ERROR.getMessage(),
                        returnType, notifyDTOList);
            }
            return ResponseFactory.exception(nonBlockValidateException, returnType);
        } catch (ValidationException e) {
            LOGGER.warn("param validation error, method:{}, parameters:{}", methodName, args, e);
            if (e.getCause() instanceof BizRuntimeException) {
                return ResponseFactory.exception(e.getCause(), returnType);
            }
            return ResponseFactory.exception(e, returnType);
        } catch (BizRuntimeException e) {
            LOGGER.warn("biz error, method:{}, parameters:{}", methodName, args, e);
            return ResponseFactory.exception(e, returnType);
        } catch (ParamException e) {
            LOGGER.warn("param invalid, method:{}, parameters:{}", methodName, args, e);
            return ResponseFactory.fail(e.getCode(), e.getMsg(), returnType);
        } catch (ValidateException e) {
            LOGGER.warn("validate exception, method:{}, parameters:{}", methodName, args, e);
            return ResponseFactory.fail(e.getCode(), e.getMessage(), e.getFailureInfo(), returnType);
        } catch (Throwable e) {
            LOGGER.error("internal error, method:{}, parameters:{}", methodName, args, e);
            return ResponseFactory.fail(BaseErrorCodeEnum.SYSTEM_ERROR, returnType);
        } finally {
            UserContextDTOUtil.removeUserContextDTO();
            BaseRequestContextUtils.removeThreadLocal();
            //请留意:NotifyContext线程上下文在NonBlockValidateAspect切面中进行了设置，这里进行销毁
            NotifyContextUtils.removeThreadLocal();
        }
    }

    private void doLimit(Object[] args, String entrance) {
//        if (Objects.isNull(args[0]) || !(args[0] instanceof UserContextDTO)) {
//            return;
//        }
//
//        //如果参数中不包含 UserContext信息，不做限频处理
//            UserContextDTO userContextTO = (UserContextDTO) args[0];
//        if (userContextTO.getUserId() == null || userContextTO.getUserId() == 0) {
//            return;
//        }
//
//        //构造参数
//        long userId = userContextTO.getUserId();
//        Map<String, String> params = Maps.newHashMapWithExpectedSize(EXPECTED_SIZE);
//        params.put(OneLimiterConstants.UUID, String.valueOf(userId));
//        params.put(APP_KEY, appName);
//
//        //调用rhino限频
//        LimitResult result = ONE_LIMITER.run(entrance, params);
//        if (!result.isPass()) {
//            throw new BizRuntimeException(BaseErrorCodeEnum.OPERATE_TOO_FAST_ERROR);
//        }
    }

    protected void checkTokenAndSetContextToThreadLocal(Object[] args) {
        throw new UnsupportedOperationException();
    }

    private void validate(Class<?> clazz, Method method, Object[] args) throws Throwable {
        // 静态校验
        try {
            StaticValidatorExecutor.validateControllerParams(getCustomJSR303Validator(), args);
        } catch (ValidateException validateException) {
            String message = validateException.getMessage();
//            // 打点map
//            Map<String, String> extraMetric = Maps.newHashMap();
//            // 参数校验失败时，添加指标打点
//            extraMetric.put("errorMsg", message);
//            // 记录参数校验出错的方法名称
//            extraMetric.put("methodName", method.getName());
//            Cat.logMetricForCount(PARAMETER_ERROR_BUSINESS_KEY, extraMetric);
            throw validateException;
        }

        if (useBizValidator()) {
            // 动态校验
            BizValidatorExecutor.validate(clazz, method, args);
        }


    }

    /**
     * 获取自定义的jsr303校验器
     *
     * @return
     */
    protected Validator getCustomJSR303Validator() {
        return null;
    }

    /**
     * 是否启用业务动态校验器
     *
     * @return
     */
    protected boolean useBizValidator() {
        return false;
    }

    /**
     * 是否开启事务执行校验
     * @return
     */
    protected TransactionTemplate getTransactionTemplate() {
        return null;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.beanDefinitionRegistry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (StringUtils.isBlank(this.inspectPath)) {
            throw new RuntimeException("请初始化inspectPath的值");
        }
        if (event instanceof ContextRefreshedEvent) {  // 某一个事件触发时
            try {
                BizValidatorExecutor.init(event.getApplicationContext(), beanDefinitionRegistry, getCustomInspectPackagePath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 自定义校验器类扫描包路径
     *
     * @return
     */
    protected String getCustomInspectPackagePath() {
        return inspectPath;
    }

    public void setInspectPath(String inspectPath) {
        if (StringUtils.isNoneBlank(inspectPath)) {
            this.inspectPath = inspectPath;
        }
    }
}
