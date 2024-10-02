package com.lhl.tool.validate;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

public class BizValidatorContext {
    /**
     * 动态校验器默认名称后缀
     */
    private static final String DEFAULT_BIZVALIDATOR_POSTFIX = "Validator";

    private Class<?> apiImplClass;

    private Method apiImplMethod;

    private Object[] params;

    private String bizValidatorName;

    private String validateMethodName;

    private BizValidatorContext(Class<?> apiImplClass, Method apiImplMethod, Object[] params, String bizValidatorName, String validateMethodName) {
        this.apiImplClass = apiImplClass;
        this.apiImplMethod = apiImplMethod;
        this.params = params;
        this.bizValidatorName = bizValidatorName;
        this.validateMethodName = validateMethodName;
    }

    public static BizValidatorContext create(Class<?> apiImplClass, BizValidate classAnnotation,
                                             Method apiImplMethod, BizValidate methodAnnotation, Object[] params) {
        if (Objects.isNull(apiImplClass) || Objects.isNull(apiImplMethod)) {
            return null;
        }

        //校验器名优先从方法注解上取，为blank则从类注解上取，两次都取不到则执行器使用默认校验器名
        String bizValidatorName = defaultBizValidatorName(apiImplClass);
        if (Objects.nonNull(methodAnnotation) && StringUtils.isNoneBlank(methodAnnotation.bizValidator())) {
            bizValidatorName = methodAnnotation.bizValidator();
        } else if (Objects.nonNull(classAnnotation) && StringUtils.isNoneBlank(classAnnotation.bizValidator())) {
            bizValidatorName = classAnnotation.bizValidator();
        }

        //校验方法名优先从方法注解上取，取不到则执行器使用默认校验方法名
        String validateMethodName = Objects.nonNull(methodAnnotation) && StringUtils.isNoneBlank(methodAnnotation.validateMethod()) ?
                methodAnnotation.validateMethod() : defaultValidateMethodName(apiImplMethod);

        return new BizValidatorContext(apiImplClass, apiImplMethod, params, bizValidatorName, validateMethodName);
    }

    private static String defaultBizValidatorName(Class<?> clazz) {
        return StringUtils.uncapitalize(clazz.getSimpleName()) + DEFAULT_BIZVALIDATOR_POSTFIX;
    }

    private static String defaultValidateMethodName(Method method) {
        return method.getName();
    }

    public Class<?> getApiImplClass() {
        return apiImplClass;
    }

    public Method getApiImplMethod() {
        return apiImplMethod;
    }

    public Object[] getParams() {
        return params;
    }

    public String getBizValidatorName() {
        return bizValidatorName;
    }

    public String getValidateMethodName() {
        return validateMethodName;
    }

    @Override
    public String toString() {
        return "BizValidatorContext{" + "apiImplClass=" + apiImplClass + ", apiImplMethod=" + apiImplMethod
               + ", bizValidatorName='" + bizValidatorName + '\'' + ", validateMethodName='" + validateMethodName + '\''
               + '}';
    }
}
