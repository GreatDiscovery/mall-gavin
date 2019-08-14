package com.example.demo.common.api;

public class CommonResult<T> {
    private long code;
    private String msg;
    private T data;

    public CommonResult() {
    }

    public CommonResult(long code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T  data) {
        this.data = data;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }
    public static <T> CommonResult<T> failed(T data) {
        return new CommonResult<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), data);
    }
}
