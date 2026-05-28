package edu.nju.di.deptc.model;

public class Course {
    private final String cno;
    private final String cnm;
    private final String cde;
    private final Integer credit;
    private final String tea;
    private final Integer capacity;
    private final String pla;
    private final Integer cpt;
    private final String tec;
    private final Integer shareFlag;

    public Course(String cno, String cnm, String cde, Integer credit, String tea, Integer capacity) {
        this.cno = cno;
        this.cnm = cnm;
        this.cde = cde;
        this.credit = credit;
        this.tea = tea;
        this.capacity = capacity;
        this.pla = "";
        this.cpt = credit;
        this.tec = tea;
        this.shareFlag = 0;
    }

    public Course(String cno, String cnm, int cde, int cpt, String tec, String pla, String shareFlag) {
        this.cno = cno;
        this.cnm = cnm;
        this.cde = String.valueOf(cde);
        this.credit = cpt;
        this.tea = tec;
        this.capacity = 0;
        this.pla = pla;
        this.cpt = cpt;
        this.tec = tec;
        this.shareFlag = "1".equals(shareFlag) ? 1 : 0;
    }

    public String cno() {
        return cno;
    }

    public String cnm() {
        return cnm;
    }

    public String cde() {
        return cde;
    }

    public Integer credit() {
        return credit;
    }

    public String tea() {
        return tea;
    }

    public Integer capacity() {
        return capacity;
    }

    public String pla() {
        return pla;
    }

    public Integer cpt() {
        return cpt;
    }

    public String tec() {
        return tec;
    }

    public Integer shareFlag() {
        return shareFlag;
    }
}