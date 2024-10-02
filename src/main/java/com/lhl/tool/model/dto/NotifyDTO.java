package com.lhl.tool.model.dto;

public class NotifyDTO {
    private Integer notifyCode;
    private String notifyInfo;

    public NotifyDTO() {
    }

    public Integer getNotifyCode() {
        return this.notifyCode;
    }

    public void setNotifyCode(Integer notifyCode) {
        this.notifyCode = notifyCode;
    }

    public String getNotifyInfo() {
        return this.notifyInfo;
    }

    public void setNotifyInfo(String notifyInfo) {
        this.notifyInfo = notifyInfo;
    }

    @Override
    public String toString() {
        return "NotifyDTO{" +
                "notifyCode=" + notifyCode +
                ", notifyInfo='" + notifyInfo + '\'' +
                '}';
    }
}
