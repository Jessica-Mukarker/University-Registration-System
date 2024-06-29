package com.project;

public class Grade {
    private String courseCode;
    private String gradeValue; 
    private int credits;

    public Grade(String courseCode, String letterGrade, int credits) {
        this.courseCode = courseCode;
        this.gradeValue = letterGrade; 
        this.credits = credits;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getGradeValue() {
        return gradeValue;
    }

    public int getCredits() {
        return credits;
    }

    public double getGPAValue() {
        switch (gradeValue) {
            case "A": return 4.0;
            case "B+": return 3.5;
            case "B": return 3.0;
            case "C+": return 2.5;
            case "C": return 2.0;
            case "D+": return 1.5;
            case "D": return 1.0;
            default: return 0.0; 
        }
    }

    @Override
    public String toString() {
        return "Grade{" +
                "courseCode='" + courseCode + '\'' +
                ", gradeValue='" + gradeValue + '\'' +
                ", credits=" + credits +
                '}';
    }
}
