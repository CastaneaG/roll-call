package com.wangkangli.roll_call.Json;

public class Jh {
    private Boolean success;
    private String msg;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Login{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                '}';
    }
}
