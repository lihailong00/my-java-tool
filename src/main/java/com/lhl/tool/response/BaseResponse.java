package com.lhl.tool.response;

import com.lhl.tool.model.dto.Status;

public class BaseResponse<T> {
    private Status status;

    private T data;

    public BaseResponse() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}
