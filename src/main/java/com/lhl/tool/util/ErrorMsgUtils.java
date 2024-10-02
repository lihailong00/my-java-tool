package com.lhl.tool.util;

import com.lhl.tool.constant.IError;

import java.util.Objects;

public final class ErrorMsgUtils {
    private static final String MSG_CONTACT = "|";

    private static final String AND = "&";

    private ErrorMsgUtils() {
    }

    /**
     * 构建错误描述信息.
     * @param errorCode    错误码
     * @param errorMsg    错误描述
     * @param contextInfo  业务上下文信息
     * @return
     */
    public static String buildMsg(Integer errorCode, String errorMsg, Object... contextInfo) {
        if (Objects.isNull(errorCode) || Objects.isNull(errorMsg)) {
            return null;
        }

        StringBuilder builder = new StringBuilder(32);
        builder.append(errorCode).append(MSG_CONTACT).append(errorMsg);
        if (isEmpty(contextInfo)) {
            return builder.toString();
        }

        return assembleBizInfo(builder, contextInfo);
    }

    /**
     * 构建错误描述信息.
     * @param errorCode    错误码
     * @param contextInfo  业务上下文信息
     * @return
     */
    public static String buildMsg(IError errorCode, Object... contextInfo) {
        if (errorCode == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(32);
        builder.append(errorCode.getCode()).append(MSG_CONTACT).append(errorCode.getMessage());
        if (isEmpty(contextInfo)) {
            return builder.toString();
        }

        return assembleBizInfo(builder, contextInfo);
    }

    /**
     * 构建业务上下文信息.
     * 业务信息之间通过&符号拼接.
     * @param sb
     * @param contextInfo
     * @return
     */
    private static String assembleBizInfo(StringBuilder sb, Object... contextInfo) {
        sb.append(MSG_CONTACT);
        for (int index = 0; index < contextInfo.length; index++) {
            sb.append(contextInfo[index]);
            if (index != contextInfo.length - 1) {
                sb.append(AND);
            }
        }

        return sb.toString();
    }

    /**
     * 判断业务上下文信息是否为空.
     * @param bizContextInfo
     * @return
     */
    private static boolean isEmpty(Object... bizContextInfo) {
        if (bizContextInfo == null || bizContextInfo.length == 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
