package edu.nju.di.deptc.model;

public class Choice {
    private final String sno;
    private final String cno;
    private final String grade;

    public Choice(String sno, String cno, String grade) {
        this.sno = sno;
        this.cno = cno;
        this.grade = grade;
    }

    public String sno() {
        return sno;
    }

    public String cno() {
        return cno;
    }

    public String grade() {
        return grade;
    }

    public String grd() {
        return grade;
    }
}