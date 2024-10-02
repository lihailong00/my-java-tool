package com.lhl.tool.validate;

import java.util.HashMap;
import java.util.Map;

public class ValidModuleContext {
    private Map<String, Object> paramMap;

    private ValidModuleContext() {
        this.paramMap = new HashMap<>();
    }

    public static ValidModuleContext create() {
        return new ValidModuleContext();
    }

    public ValidModuleContext put(String key, Object param) {
        paramMap.put(key, param);
        return this;
    }

    public Object get(String key) {
        return paramMap.get(key);
    }
}