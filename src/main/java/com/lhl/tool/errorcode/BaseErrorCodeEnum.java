package com.lhl.tool.errorcode;

import com.lhl.tool.constant.IError;

public enum BaseErrorCodeEnum implements IError {
    SYSTEM_ERROR(100001, "系统错误"),
    PARAM_ERROR(100002, "参数错误"),

    REDIS_OP_ERROR(999003, "REDIS操作异常"),

    DB_OP_ERROR(999004, "DB操作异常"),

    LOGIN_ERROR(999005, "未登录异常"),

    SERVICE_TIMEOUT(999006, "服务超时，请稍后重试"),

    PERMISSION_DENIED(999007, "权限不足"),

    OPERATE_TOO_FAST_ERROR(999008, "操作过于频繁，请稍后再试"),

    NETWORK_EXCEPTION(999009, "网络异常"),

    CODE_POST_GEN_FAILED(999010, "确认生成编码失败"),

    SERVER_BUSY_RETRY_LATER(999011, "服务器忙不过来啦，请稍后重试"),

    RPC_ERROR(999012, "远程服务异常，请稍后再试"),

    GRAY_ROUTE_ERROR(999013, "灰度路由异常"),

    PLUS_VERSION_SERVICE_ERROR(999014, "版本服务异常，请稍后再试"),

    SHARDING_KEY_NOT_EXIST_ERROR(999015, "分库分表字段未传值异常"),


    NON_BLOCK_VALIDATOR_ERROR(1000000, "非阻塞式校验返回的异常信息,请读取notifyDTOs显示详情");
    private final Integer errorCode;

    private final String errorMsg;

    private BaseErrorCodeEnum(Integer errorCode, String errorMsg) {
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
