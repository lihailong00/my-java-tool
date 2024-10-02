/**
 * Meituan.com Inc.
 * Copyright (c) 2010-2021 All Rights Reserved.
 */
package com.lhl.tool.validate;

import com.lhl.tool.errorcode.BaseErrorCodeEnum;
import com.lhl.tool.exception.BizException;
import com.lhl.tool.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态校验执行器
 **/
@Slf4j
public class BizValidatorExecutor {

    /**
     * 单例
     */
    private static final BizValidatorExecutor INSTANCE = new BizValidatorExecutor();

    /**
     * 是否完成初始化
     */
    private static Boolean INITIALIZED = false;

    /**
     * 类扫描器
     */
    private ClassPathBeanDefinitionScanner scanner;

    /**
     * spring上下文
     */
    private ApplicationContext applicationContext;

    /**
     * 是否需要动态校验缓存
     */
    private final Map<Method, Boolean> needValidateCacheMap = new ConcurrentHashMap<>();

    /**
     * 启动动态校验框架
     * @param applicationContext spring上下文
     * @param inspectPath 自检扫描的实现类路径
     * @throws Exception
     */
    public static void init(ApplicationContext applicationContext, BeanDefinitionRegistry registry, String inspectPath) throws Exception {
        if (INITIALIZED) {
            return;
        }

        // 初始化参数
        INSTANCE.scanner = new ClassPathBeanDefinitionScanner(registry);
        INSTANCE.applicationContext = applicationContext;

        // 动态校验器自检，自检异常终止启动
        inspect(inspectPath);

        INITIALIZED = true;
    }

    /**
     * 执行校验
     * @param clazz
     * @param method
     * @param params
     * @throws Throwable
     */
    public static void validate(Class<?> clazz, Method method, Object[] params) throws Throwable {
        BizValidate classAnnotation = AnnotationUtils.findAnnotation(clazz, BizValidate.class);
        BizValidate methodAnnotation = AnnotationUtils.findAnnotation(method, BizValidate.class);

        // 没有动态校验器或判断注解不需要校验，直接返回
        if (!needValidate(clazz, classAnnotation, method, methodAnnotation)) {
            return;
        }

        // 组装上下文
        BizValidatorContext context = BizValidatorContext.create(clazz, classAnnotation, method, methodAnnotation, params);
        // 执行校验
        try {
            Object bizValidator = INSTANCE.applicationContext.getBean(context.getBizValidatorName());
            Method validateMethod = bizValidator.getClass()
                    .getMethod(context.getValidateMethodName(), context.getApiImplMethod().getParameterTypes());
            validateMethod.invoke(bizValidator, context.getParams());
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * 自检，检查指定路径下，一个controllerValidator类的函数是否有对应的原函数
     * @param inspectPath
     * @throws Exception
     */
    private static void inspect(String inspectPath) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("动态校验context预检开始, inspectPath = {}", inspectPath);
        // 获取自检路径下所有类
        Set<Class<?>> classSet = scanClass(inspectPath);
        if (CollectionUtils.isEmpty(classSet)) {
            throw new BizException(BaseErrorCodeEnum.SYSTEM_ERROR.getCode(),
                    String.format("自检路径下没有类文件，inspectPath:%s", inspectPath));
        }
        long step1Time = System.currentTimeMillis();
        log.info("动态校验context包扫描完成, 共{}个类文件, 耗时{}ms", classSet.size(), step1Time - startTime);

        // 自检上下文
        List<BizValidatorContext> contextList = new LinkedList<>();
        for (Class<?> clazz : classSet) {
            BizValidate classAnnotation = AnnotationUtils.findAnnotation(clazz, BizValidate.class);
            // 获取类声明的所有公共方法
            for (Method method : ClassUtil.getDeclaredPublicMethods(clazz)) {
                BizValidate methodAnnotation = AnnotationUtils.findAnnotation(method, BizValidate.class);
                // 若该方法需要校验，组装上下文用于自检
                if (needValidate(clazz, classAnnotation, method, methodAnnotation)) {
                    contextList.add(BizValidatorContext.create(clazz, classAnnotation, method, methodAnnotation, null));
                }
            }
        }

        // 执行自检，保证每个上下文必然能找到对应的校验方法不影响业务，但不保证每个校验方法都有对应的上下文
        for (BizValidatorContext context : contextList) {
            try {
                INSTANCE.applicationContext.getBean(context.getBizValidatorName())
                        .getClass()
                        .getMethod(context.getValidateMethodName(), context.getApiImplMethod().getParameterTypes());
            } catch (NoSuchBeanDefinitionException e) {
                throw new BizException(BaseErrorCodeEnum.SYSTEM_ERROR.getCode(),
                        String.format("自检未发现对应校验器，class:%s", context.getApiImplClass().getName()));
            } catch (NoSuchMethodException e) {
                throw new BizException(BaseErrorCodeEnum.SYSTEM_ERROR.getCode(),
                        String.format("自检未发现对应校验方法，class:%s，method:%s", context.getApiImplClass().getName(), context.getApiImplMethod().getName()));
            }
        }
        long step2Time = System.currentTimeMillis();
        log.info("动态校验context校验完成, 耗时{}ms, 校验器信息{}", step2Time - step1Time, contextList);
    }

    /**
     * 判断是否需要校验
     * @param clazz
     * @param classAnnotation
     * @param method
     * @param methodAnnotation
     * @return
     */
    private static Boolean needValidate(Class<?> clazz, BizValidate classAnnotation,
                                        Method method, BizValidate methodAnnotation) {
        // 从缓存取是否需要校验
        if (INSTANCE.needValidateCacheMap.containsKey(method)) {
            return true;
        }

        // 非公共类或非公共方法不进行校验
        if (!Modifier.isPublic(clazz.getModifiers()) || !Modifier.isPublic(method.getModifiers())) {
            return false;
        }

        // 类和方法都没有注解不进行校验
        if (Objects.isNull(classAnnotation) && Objects.isNull(methodAnnotation)) {
            return false;
        }

        // 类注解上如果ignore=true不进行校验
        if (Objects.nonNull(classAnnotation) && classAnnotation.ignore()) {
            return false;
        }

        // 方法注解上如果ignore=true不进行校验
        if (Objects.nonNull(methodAnnotation) && methodAnnotation.ignore()) {
            return false;
        }

        INSTANCE.needValidateCacheMap.put(method, true);
        return true;
    }

    /**
     * 扫描自检路径下的所有类
     * @param inspectPath
     * @return
     * @throws Exception
     */
    private static Set<Class<?>> scanClass(String inspectPath) throws Exception {
        Set<BeanDefinition> beanDefinitionSet = INSTANCE.scanner.findCandidateComponents(inspectPath);
        Set<Class<?>> res = new HashSet<>();
        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            res.add(Class.forName(beanDefinition.getBeanClassName()));
        }
        return res;
    }
}
