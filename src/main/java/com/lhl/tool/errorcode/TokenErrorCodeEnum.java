package com.lhl.tool.errorcode;

import com.lhl.tool.constant.IError;

public enum TokenErrorCodeEnum implements IError {
    INVALID_TOKEN(300001, "无效签名"),
    TOKEN_OUT_OF_DATE(300002, "token过期"),
    TOKEN_ALGORITHM_ERROR(300003, "token算法不一致"),
    TOKEN_OTHER_ERROR(300004,"token其他错误")
    ;
    private final Integer errorCode;

    private final String errorMsg;

    private TokenErrorCodeEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public int getCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        return this.errorMsg;
    }
}
