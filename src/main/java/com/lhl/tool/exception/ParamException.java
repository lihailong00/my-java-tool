package com.lhl.tool.exception;

/**
 * <p>参数异常，可以动态自定义错误码和描述信息</p>
 *
 * @author liyang121@meituan.com
 * @version ParamException.class v4.6.0 2020-03-08 下午07:30
 * @since v4.6.0
 **/
public class ParamException extends RuntimeException{
    private Integer code;//错误码
    private String msg;//异常信息

    public ParamException(String msg){
        super(msg);
        this.msg = msg;
    }

    public ParamException(Throwable ex){
        super(ex);
    }

    public ParamException(String msg, Throwable ex){
        super(msg, ex);
        this.msg = msg;
    }

    public ParamException(Integer code, String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ParamException(Integer code, String msg, Throwable ex){
        super(msg, ex);
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}