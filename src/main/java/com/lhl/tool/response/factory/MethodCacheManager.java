package com.lhl.tool.response.factory;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MethodCacheManager {
    private static final String DOT =".";

    private static final String SET_DATA = "setData";

    private static final String SET_STATUS = "setStatus";

    private static final ConcurrentHashMap<String, Method> cacheMap = new ConcurrentHashMap<>();

    private MethodCacheManager() {
    }

    public static Method fetchMethod(Class<?> clazz, String methodName) {
        if (clazz == null || isEmpty(methodName)) {
            return null;
        }
        String cacheKey = buildCacheKey(clazz.getName(), methodName);
        Method method = cacheMap.get(cacheKey);
        if (method == null) {
            initCaches(clazz);
        }
        method = cacheMap.get(cacheKey);
        return method;
    }

    private static void initCaches(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (needCache(method.getName())) {
                cacheMap.put(buildCacheKey(clazz.getName(), method.getName()), method);
            }
        }
    }

    private static boolean needCache(String methodName) {
        return Objects.equals(methodName, SET_DATA) || Objects.equals(methodName, SET_STATUS);
    }

    private static String buildCacheKey(String className, String methodName) {
        return className + DOT + methodName;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
