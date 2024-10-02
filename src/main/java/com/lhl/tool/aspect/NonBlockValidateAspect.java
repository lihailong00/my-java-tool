/**
 * Meituan.com Inc.
 * Copyright (c) 2010-2020 All Rights Reserved.
 */
package com.lhl.tool.aspect;

import com.lhl.tool.exception.ValidateException;
import com.lhl.tool.model.dto.NotifyDTO;
import com.lhl.tool.request.BaseRequest;
import com.lhl.tool.util.BaseRequestContextUtils;
import com.lhl.tool.util.NotifyContextUtils;
import io.micrometer.common.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * <p>
 * 非阻塞式校验切面，用于处理非阻塞式校验Module的validate返回异常信息，将异常信息组装成对象置入线程上下文中
 * </p>
 *
 * @author liping18@meituan.com
 * @version NonBlockValidateAspect.class  2020-10-21 7:29 PM
 * @since sjst.scm_v5.16
 **/
public class NonBlockValidateAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockValidateAspect.class);

    /**
     * 切面的具体处理逻辑
     *
     * @param joinPoint 切面上下文
     * @return
     */
    public Object doPointCut(ProceedingJoinPoint joinPoint) throws Throwable {

        //跳过验证则返回null
        if (isSkipValidate()) {
            return null;
        }

        try {
            return joinPoint.proceed();
        } catch (ValidateException validateException) {
            //将验证抛出的异常信息设置到线程上下文中
            setNotifyContext(validateException);

            return null;
        }
    }

    /**
     * 将异常信息设置到线程上下文中
     *
     * @param validateException
     */
    private void setNotifyContext(ValidateException validateException) {
        int code = validateException.getCode();
        String notifyInfo = validateException.getFailureInfo();

        if (StringUtils.isBlank(notifyInfo)) {
            return;
        }

        NotifyDTO notifyDTO = new NotifyDTO();
        notifyDTO.setNotifyCode(code);
        notifyDTO.setNotifyInfo(notifyInfo);

        NotifyContextUtils.addNotifyDTO(notifyDTO);

        LOGGER.info("NonBlockValidate:添加到线程上下文中,notifyDTO:{}", notifyDTO);

    }

    /**
     * 检查是否需要跳过校验：线程上下文中BaseRequest的nonBlockValidIgnore字段为ture时表示跳过验证
     *
     * @return true -- 跳过, false -- 不跳过
     */
    protected boolean isSkipValidate() {
        BaseRequest baseRequest = BaseRequestContextUtils.getBaseRequestContext();
        if (Objects.nonNull(baseRequest)
                && Objects.nonNull(baseRequest.getNonBlockVerifyIgnore())
                && baseRequest.getNonBlockVerifyIgnore()) {
            return true;
        }

        return false;
    }

}