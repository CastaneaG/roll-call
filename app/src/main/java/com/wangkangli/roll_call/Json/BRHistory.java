package com.wangkangli.roll_call.Json;

public class BRHistory {
    private String id;
    private String fyhid;
    private String fqdsj;
    private String fqdjd;
    private String fqdwd;
    private String fflag;
    private String fyhm;
    private String fqssj;
    private String fjssj;
    private String fqddate;

    @Override
    public String toString() {
        return "BRHistory{" +
                "id='" + id + '\'' +
                ", fyhid='" + fyhid + '\'' +
                ", fqdsj='" + fqdsj + '\'' +
                ", fqdjd='" + fqdjd + '\'' +
                ", fqdwd='" + fqdwd + '\'' +
                ", fflag='" + fflag + '\'' +
                ", fyhm='" + fyhm + '\'' +
                ", fqssj='" + fqssj + '\'' +
                ", fjssj='" + fjssj + '\'' +
                ", fqddate='" + fqddate + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFyhid() {
        return fyhid;
    }

    public void setFyhid(String fyhid) {
        this.fyhid = fyhid;
    }

    public String getFqdsj() {
        return fqdsj;
    }

    public void setFqdsj(String fqdsj) {
        this.fqdsj = fqdsj;
    }

    public String getFqdjd() {
        return fqdjd;
    }

    public void setFqdjd(String fqdjd) {
        this.fqdjd = fqdjd;
    }

    public String getFqdwd() {
        return fqdwd;
    }

    public void setFqdwd(String fqdwd) {
        this.fqdwd = fqdwd;
    }

    public String getFflag() {
        return fflag;
    }

    public void setFflag(String fflag) {
        this.fflag = fflag;
    }

    public String getFyhm() {
        return fyhm;
    }

    public void setFyhm(String fyhm) {
        this.fyhm = fyhm;
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

    public String getFqddate() {
        return fqddate;
    }

    public void setFqddate(String fqddate) {
        this.fqddate = fqddate;
    }
}
