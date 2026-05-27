package com.educ.departmentA.model;

public class ChoiceCourse {
    private String courseId;
    private String studentId;
    private String score;

    public ChoiceCourse() {
    }

    public ChoiceCourse(String courseId, String studentId, String score) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.score = score;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ChoiceCourse{" +
                "courseId='" + courseId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
