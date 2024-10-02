package com.lhl.tool.model.dto;

import java.util.List;

public class Status {

    private Integer code = 0;

    // msg 是提示前端的词
    private String msg = "成功";

    private String failureInfo;

    private List<NotifyDTO> notifyDTOs;

    public Status() {
    }

    public Status(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Status(Integer code, String msg, String failureInfo) {
        this.code = code;
        this.msg = msg;
        this.failureInfo = failureInfo;
    }

    public Status(Integer code, String msg, List<NotifyDTO> notifyDTOs) {
        this.code = code;
        this.msg = msg;
        this.notifyDTOs = notifyDTOs;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFailureInfo() {
        return this.failureInfo;
    }

    public void setFailureInfo(String failureInfo) {
        this.failureInfo = failureInfo;
    }

    public List<NotifyDTO> getNotifyDTOs() {
        return this.notifyDTOs;
    }

    public void setNotifyDTOs(List<NotifyDTO> notifyDTOs) {
        this.notifyDTOs = notifyDTOs;
    }

    @Override
    public String toString() {
        return "Status{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", failureInfo='" + failureInfo + '\'' +
                ", notifyDTOs=" + notifyDTOs +
                '}';
    }

    public boolean isSuccess() {
        return this.code != null && this.code == 0;
    }

    public boolean isFailed() {
        return this.code == null || this.code != 0;
    }
}