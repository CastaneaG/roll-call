package com.wangkangli.roll_call.Json;

public class Qdrq {
    private String fqsrq;
    private String fjsrq;

    @Override
    public String toString() {
        return "Qdrq{" +
                "fqsrq='" + fqsrq + '\'' +
                ", fjsrq='" + fjsrq + '\'' +
                '}';
    }

    public String getFqsrq() {
        return fqsrq;
    }

    public void setFqsrq(String fqsrq) {
        this.fqsrq = fqsrq;
    }

    public String getFjsrq() {
        return fjsrq;
    }

    public void setFjsrq(String fjsrq) {
        this.fjsrq = fjsrq;
    }
}
