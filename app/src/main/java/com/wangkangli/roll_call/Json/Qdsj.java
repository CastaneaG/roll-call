package com.wangkangli.roll_call.Json;

public class Qdsj {
    private String id;
    private String fqssj;
    private String fjssj;

    @Override
    public String toString() {
        return "Qdsj{" +
                "id='" + id + '\'' +
                ", fqssj='" + fqssj + '\'' +
                ", fjssj='" + fjssj + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFqssj() {
        return fqssj;
    }

    public void setFqssj(String fqssj) {
        this.fqssj = fqssj;
    }

    public String getFjssj() {
        return fjssj;
    }

    public void setFjssj(String fjssj) {
        this.fjssj = fjssj;
    }
}
