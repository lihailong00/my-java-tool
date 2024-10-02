package com.lhl.tool.request;

import com.lhl.tool.constant.IError;
import com.lhl.tool.model.dto.UserContextDTO;

import java.util.Objects;

public class BaseRequest {
    private Boolean nonBlockVerifyIgnore;

    public BaseRequest() {
    }

    public IError validate(UserContextDTO userContext) {
        return null;
    }

    public IError validate() {
        return null;
    }

    public Boolean getNonBlockVerifyIgnore() {
        return this.nonBlockVerifyIgnore;
    }

    public void setNonBlockVerifyIgnore(Boolean nonBlockVerifyIgnore) {
        this.nonBlockVerifyIgnore = nonBlockVerifyIgnore;
    }

    @Override
    public String toString() {
        return "BaseRequest{" +
                "nonBlockVerifyIgnore=" + nonBlockVerifyIgnore +
                '}';
    }
}