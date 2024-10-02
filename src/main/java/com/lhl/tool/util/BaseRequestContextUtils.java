package com.lhl.tool.util;

import com.lhl.tool.request.BaseRequest;

public final class BaseRequestContextUtils {
    private static ThreadLocal<BaseRequest> baseRequestThreadLocal = new ThreadLocal<>();

    private BaseRequestContextUtils() {
        // do nothing
    }

    public static BaseRequest getBaseRequestContext() {
        return baseRequestThreadLocal.get();
    }


    public static void setBaseRequestContext(BaseRequest baseRequest) {
        baseRequestThreadLocal.set(baseRequest);
    }

    public static void removeThreadLocal() {
        baseRequestThreadLocal.remove();
    }


}