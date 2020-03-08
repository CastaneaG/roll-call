package com.wangkangli.roll_call.Json;

public class Qd {
    private String success;
    private String msg;

    @Override
    public String toString() {
        return "Qd{" +
                "success='" + success + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getSuccess() {

        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
