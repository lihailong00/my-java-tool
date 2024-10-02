package com.lhl.tool.exception;

import com.lhl.tool.constant.IError;

public class BizException extends Exception implements IError {
    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(IError iError) {
        this(iError.getCode(), iError.getMessage());
    }

    public int getCode() {
        return this.code;
    }
}