package com.lhl.tool.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassUtil {
    /**
     * 获取类自己声明的公共方法
     * @param clazz
     * @return
     */
    public static Set<Method> getDeclaredPublicMethods(Class<?> clazz) {
        Method[] declaredMethodArr = clazz.getDeclaredMethods();
        if (declaredMethodArr.length == 0) {
            return new HashSet<>();
        }

        return Arrays.stream(declaredMethodArr)
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .collect(Collectors.toSet());
    }
}
