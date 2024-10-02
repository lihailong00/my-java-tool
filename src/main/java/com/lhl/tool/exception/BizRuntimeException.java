package com.lhl.tool.exception;

import com.lhl.tool.constant.IError;
import com.lhl.tool.util.ErrorMsgUtils;

public class BizRuntimeException extends RuntimeException {
    /**
     * 错误编码
     */
    private IError errorCode;

    /**
     * 传入错误码构建自定义业务异常.
     * @param errorCode
     */
    public BizRuntimeException(IError errorCode) {
        super(ErrorMsgUtils.buildMsg(errorCode));
        this.setErrorCode(errorCode);
    }

    /**
     * 推荐构造方法，传入错误码和业务上下文信息构建自定义业务异常.
     * <p>
     *     传入业务上下文信息是为了便于打印堆栈信息记录对应的业务上线文信息，便于排查问题。bizContextInfo是一个可变数组，每个item都是类似orderId=xxx
     *     这样的键值对。
     * </p>
     * @param errorCode
     * @param bizContextInfo
     */
    public BizRuntimeException(IError errorCode, Object... bizContextInfo) {
        super(ErrorMsgUtils.buildMsg(errorCode, bizContextInfo));
        this.setErrorCode(errorCode);
    }

    /**
     * 自定义错误描述错误的业务异常
     *
     * @param errorCode 错误码
     * @param errorMsg  错误描述
     * @param bizContextInfo    异常现场
     */
    public BizRuntimeException(IError errorCode, String errorMsg, Object... bizContextInfo) {
        super(ErrorMsgUtils.buildMsg(errorCode.getCode(), errorMsg, bizContextInfo));
        this.setErrorCode(errorCode);
    }

    /**
     * 推荐构造方法，传入错误码和业务上下文信息构建自定义业务异常.
     * <p>
     *     传入业务上下文信息是为了便于打印堆栈信息记录对应的业务上线文信息，便于排查问题。bizContextInfo是一个可变数组，每个item都是类似orderId=xxx
     *     这样的键值对.
     * </p>
     * @param errorCode
     * @param causes
     * @param bizContextInfo
     */
    public BizRuntimeException(IError errorCode, Throwable causes, Object... bizContextInfo) {
        super(ErrorMsgUtils.buildMsg(errorCode, bizContextInfo), causes);
        this.setErrorCode(errorCode);
    }

    /**
     * 传入整型的错误码与异常信息，构建自定义业务异常。
     * @param code 整型的错误码
     * @param msg 异常信息
     */
    public BizRuntimeException(Integer code, String msg){
        super(msg);
        this.setErrorCode(new BaseErrorCode(code,msg));
    }

    public IError getErrorCode() {
        return errorCode;
    }

    private void setErrorCode(IError errorCode) {
        this.errorCode = errorCode;
    }

    class BaseErrorCode implements IError{
        private  Integer errorCode;
        private  String errorMsg;

        BaseErrorCode(Integer errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }

        @Override
        public int getCode() {
            return errorCode;
        }

        @Override
        public String getMessage() {
            return errorMsg;
        }
    }
}
