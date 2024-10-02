/**
 * Meituan.com Inc.
 * Copyright (c) 2010-2021 All Rights Reserved.
 */
package com.lhl.tool.validate;


import com.lhl.tool.constant.IError;
import com.lhl.tool.errorcode.BaseErrorCodeEnum;
import com.lhl.tool.exception.ValidateException;
import com.lhl.tool.model.dto.UserContextDTO;
import com.lhl.tool.request.BaseRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.commons.collections4.CollectionUtils;


import java.util.Objects;
import java.util.Set;

/**
 * <p>
 *     静态校验执行器
 * </p>
 *
 * @author liupeiwen02@meituan.com
 * @version StaticValidatorExecutor.class  2021-02-03 16:22
 * @since
 **/
public class StaticValidatorExecutor {

    private static final Validator defaultValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final int       LENGTH_ONE       = 1;
    private static final int       LENGTH_TWO       = 2;

    /**
     * thrift参数静态校验
     * @param validatorInstance
     * @param params
     */
    public static void validateControllerParams(Validator validatorInstance, Object[] params) {
        Object request = null;
        for (Object param : params) {
            if (param instanceof BaseRequest) {
                request = param;
                break;
            }
        }
        if (request == null) {
            return;
        }

        // jsr303校验
        if (validatorInstance == null) {
            StaticValidatorExecutor.validate(defaultValidator, request);
        } else {
            StaticValidatorExecutor.validate(validatorInstance, request);
        }

        // 如果继承了BaseRequest，做个性化的参数校验
        if (request instanceof BaseRequest) {
            if (params[0] instanceof UserContextDTO) {
                IError errorCode = ((BaseRequest) request).validate((UserContextDTO) params[0]);
                if (Objects.nonNull(errorCode)) {
                    throw new ValidateException(errorCode);
                }
            }
            IError errorCode = ((BaseRequest) request).validate();
            if (Objects.nonNull(errorCode)) {
                throw new ValidateException(errorCode);
            }
        }
    }

    /**
     * 默认jsr303校验
     * @param validatorInstance
     * @param param
     * @param groups
     */
    public static void validate(Validator validatorInstance, Object param, Class<?>... groups) {
        Set<ConstraintViolation<Object>> validateResult = validatorInstance.validate(param, groups);
        if (CollectionUtils.isNotEmpty(validateResult)) {
            throw new ValidateException(BaseErrorCodeEnum.PARAM_ERROR.getCode(),
                    validateResult.iterator().next().getMessage());
        }
    }
}
