package com.lhl.tool.exception;

import com.lhl.tool.constant.IError;
import lombok.Getter;

public class TokenInvalidException extends RuntimeException implements IError {
    private static final int DEFAULT_ERROR_CODE = 400;
    private final int code;

    /**
     * 异常数据对象json字符串
     */
    @Getter
    private String failureInfo;

    public TokenInvalidException(String message) {
        super(message);
        this.code = DEFAULT_ERROR_CODE;
    }

    public TokenInvalidException(){
        this.code = DEFAULT_ERROR_CODE;
    }

    public TokenInvalidException(IError iError) {
        this(iError.getCode(), iError.getMessage());
    }

    public TokenInvalidException(int code, String message) {
        super(message);
        this.code = code;
    }

    public TokenInvalidException(int code, String message, String failureInfo) {
        super(message);
        this.code = code;
        this.failureInfo = failureInfo;
    }

    public TokenInvalidException(IError iError, String failureInfo) {
        this(iError.getCode(), iError.getMessage(), failureInfo);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "TokenInvalidException{" +
                "code=" + code +
                ", failureInfo='" + failureInfo + '\'' +
                '}';
    }
}
