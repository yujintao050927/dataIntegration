package edu.nju.di.deptc.model;

public class Student {
    private final String sno;
    private final String snm;
    private final String sex;
    private final String sde;
    private final String pwd;

    public Student(String sno, String snm, String sex, String sde, String pwd) {
        this.sno = sno;
        this.snm = snm;
        this.sex = sex;
        this.sde = sde;
        this.pwd = pwd;
    }

    public String sno() {
        return sno;
    }

    public String snm() {
        return snm;
    }

    public String sex() {
        return sex;
    }

    public String sde() {
        return sde;
    }

    public String pwd() {
        return pwd;
    }
}