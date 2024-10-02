package com.lhl.tool.exception;


import com.lhl.tool.constant.IError;
import lombok.Getter;

/**
 * 参数校验异常
 **/
public class ValidateException extends RuntimeException implements IError {

    private static final int DEFAULT_ERROR_CODE = 400;
    private final int code;

    /**
     * 异常数据对象json字符串
     */
    @Getter
    private String failureInfo;

    public ValidateException(String message) {
        super(message);
        this.code = DEFAULT_ERROR_CODE;
    }

    public ValidateException(){
        this.code = DEFAULT_ERROR_CODE;
    }

    public ValidateException(IError iError) {
        this(iError.getCode(), iError.getMessage());
    }

    public ValidateException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ValidateException(int code, String message, String failureInfo) {
        super(message);
        this.code = code;
        this.failureInfo = failureInfo;
    }

    public ValidateException(IError iError, String failureInfo) {
        this(iError.getCode(), iError.getMessage(), failureInfo);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ValidateException{" +
                "code=" + code +
                ", failureInfo='" + failureInfo + '\'' +
                '}';
    }
}
