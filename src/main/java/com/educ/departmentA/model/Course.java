package com.educ.departmentA.model;

public class Course {
    private String courseId;
    private String courseName;
    private String credit;
    private String teacher;
    private String location;
    private String isShared;
    private String department; // 用于标识课程所属院系（在共享课程中使用）

    public Course() {
    }

    public Course(String courseId, String courseName, String credit, String teacher, String location, String isShared) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credit = credit;
        this.teacher = teacher;
        this.location = location;
        this.isShared = isShared;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIsShared() {
        return isShared;
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credit='" + credit + '\'' +
                ", teacher='" + teacher + '\'' +
                ", location='" + location + '\'' +
                ", isShared='" + isShared + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
