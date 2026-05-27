package com.educ.departmentA.model;

public class Student {
    private String studentId;
    private String studentName;
    private String gender;
    private String department;
    private String accountName;

    public Student() {
    }

    public Student(String studentId, String studentName, String gender, String department, String accountName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.gender = gender;
        this.department = department;
        this.accountName = accountName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                ", accountName='" + accountName + '\'' +
                '}';
    }
}
