package com.project;

import java.util.*;

public class Major {
    private String major;
    private List<String> courses;
    
    public Major (String major) {
        this.major = major;
    }

     public Major (String major,List<String> courses) {
        this.major = major;
        this.courses=courses;
    }
    public String getMajor() {
        return major;
    }
    public List<String> getCourses() {
        return courses;
    }

    //add courses to this major
    public void addCourse(String course) {
        Optional<List<String>> oc = Optional.ofNullable(courses);
    
        if (oc.isPresent()) {
            oc.ifPresent(e -> courses.add(course));
        } else {
            Set<String> newSet = new HashSet<>();
            newSet.add(course);
            courses = new ArrayList<>(newSet);
        }
    }
    
}
