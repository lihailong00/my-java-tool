package com.lhl.tool.response.factory;

import com.lhl.tool.constant.IError;
import com.lhl.tool.errorcode.BaseErrorCodeEnum;
import com.lhl.tool.exception.BizRuntimeException;
import com.lhl.tool.exception.TokenInvalidException;
import com.lhl.tool.exception.ValidateException;
import com.lhl.tool.model.dto.NotifyDTO;
import com.lhl.tool.model.dto.Status;
import com.lhl.tool.response.CommonResponse;

import java.lang.reflect.Method;
import java.util.List;

public class ResponseFactory {
    private static final String SET_DATA = "setData";

    private static final String SET_STATUS = "setStatus";

    private ResponseFactory() {
    }

    public static CommonResponse success() {
        CommonResponse response = new CommonResponse();
        response.setStatus(new Status());
        return response;
    }

    public static <T1, T2> T2 success(T1 data, Class<T2> clazz) {
        if (clazz == null) {
            throw new BizRuntimeException(BaseErrorCodeEnum.SYSTEM_ERROR);
        }
        Method setData = MethodCacheManager.fetchMethod(clazz, SET_DATA);
        Method setStatus = MethodCacheManager.fetchMethod(clazz, SET_STATUS);
        if (setData == null || setStatus == null) {
            throw new BizRuntimeException(BaseErrorCodeEnum.SYSTEM_ERROR);
        }
        try {
            T2 obj = clazz.newInstance();
            setStatus.invoke(obj, new Status());
            setData.invoke(obj, data);
            return obj;
        } catch (Exception e) {
            throw new BizRuntimeException(BaseErrorCodeEnum.SYSTEM_ERROR, e);
        }
    }

    public static <T> T fail(IError iError, Class<T> clazz) {
        return fail(iError.getCode(), iError.getMessage(), (String) null, clazz);
    }

    public static <T> T fail(Integer errorCode, String errorMsg, Class<T> clazz) {
        return fail(errorCode, errorMsg, (String) null, clazz);
    }

    public static <T> T fail(Integer errorCode, String errorMsg, String failureInfo, Class<T> clazz) {
        if (clazz == null) {
            throw new BizRuntimeException(BaseErrorCodeEnum.SYSTEM_ERROR);
        }
        Method setStatus = MethodCacheManager.fetchMethod(clazz, SET_STATUS);
        if (setStatus == null) {
            throw new BizRuntimeException(BaseErrorCodeEnum.SYSTEM_ERROR);
        }
        try {
            Status status = new Status();
            status.setCode(errorCode);
            status.setMsg(errorMsg);

            T obj = clazz.newInstance();
            setStatus.invoke(obj, new Status(errorCode, errorMsg, failureInfo));

            return obj;
        } catch (Exception e) {
            throw new BizRuntimeException(BaseErrorCodeEnum.SYSTEM_ERROR, e);
        }
    }

    /**
     * 使用指定的errorCode、errorMsg、notifyDTOs构造返回一个失败的结果对象.
     *
     * @param errorCode  错误码
     * @param errorMsg   错误描述
     * @param notifyDTOs notifyDTOs
     * @param clazz      接口响应结果类型
     * @param <T>        接口响应结果类型
     * @return 接口响应结果
     */
    public static <T> T fail(Integer errorCode, String errorMsg, Class<T> clazz, List<NotifyDTO> notifyDTOs) {
        if (clazz == null) {
            throw new BizRuntimeException(BaseErrorCodeEnum.SYSTEM_ERROR);
        }

        Method setStatus = MethodCacheManager.fetchMethod(clazz, SET_STATUS);

        if (setStatus == null) {
            throw new BizRuntimeException(BaseErrorCodeEnum.SYSTEM_ERROR);
        }

        T obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
            Class<?> paramClazz = setStatus.getParameterTypes()[0];
            setStatus.invoke(obj, new Status(errorCode, errorMsg, notifyDTOs));
        } catch (Exception e) {
            throw new BizRuntimeException(BaseErrorCodeEnum.SYSTEM_ERROR, e);
        }

        return obj;
    }

    /**
     * 构造返回一个异常的结果对象.
     *
     * @param e     异常
     * @param clazz 接口响应结果类型
     * @param <T1>  接口响应数据类型
     * @param <T2>  掊响应结果类型
     * @return 响应结果
     */
    public static <T1, T2> T2 exception(T1 e, Class<T2> clazz) {
        if (e instanceof BizRuntimeException) {
            BizRuntimeException e1 = (BizRuntimeException) e;
            return fail(e1.getErrorCode(), clazz);
        } else if (e instanceof ValidateException) {
            ValidateException e1 = (ValidateException) e;
            return fail(e1.getCode(), e1.getMessage(), e1.getFailureInfo(), clazz);
        } else if (e instanceof TokenInvalidException) {
            TokenInvalidException e1 = (TokenInvalidException) e;
            return fail(e1.getCode(), e1.getMessage(), clazz);
        }

        return fail(BaseErrorCodeEnum.SYSTEM_ERROR, clazz);
    }
}
