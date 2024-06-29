package com.project;

import java.time.LocalDate;
import java.util.*;

public class Faculty extends UserProfile {

    private String major;
    private LocalDate dateOfBirth;
    private List<newCourse> courses; 


    public Faculty(String name, String id, String contactDetails, String password, String major, LocalDate dateOfBirth) {
        super(name, id, contactDetails, Role.FACULTY, password);
        this.major=major;
        this.dateOfBirth = dateOfBirth;
        this.courses = new ArrayList<>();
    }

    public String teachesMajor() {
        return major;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<newCourse> getCourses() {
        return courses;
    }

    public void setCourses(List<newCourse> courses) {
        this.courses = courses;
    }


   
    
}
