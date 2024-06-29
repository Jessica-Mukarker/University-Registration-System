package com.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class newSection {
    private String sectionCode; 
    private String faculty;
    private String course; 
    private List<newMeetingSession> meetingSessions; 
    private List<String> enrolledStudents;

    
    public newSection(String sectionCode, String course, String faculty, List<newMeetingSession> meetingSessions) {
        this.sectionCode = sectionCode;
        this.faculty = faculty;
        this.course = course;
        this.meetingSessions = meetingSessions;
        this.enrolledStudents = new ArrayList<>();
    }

    public newSection(String sectionCode, newCourse courseName, String facultyName,
    newCourse course, List<newMeetingSession> meetingSessions){

    }
   

   public void addMeetingSession(newMeetingSession meetingSession, ReadWriteFile rwf) throws IOException {
        if (!hasTimeConflict(meetingSession)) {
            meetingSessions.add(meetingSession);
            rwf.addOrUpdateSection(this); 
        } else {
            System.out.println("Cannot add meeting session due to time conflict.");
        }
    }

    //enroll student in this section if the student isn't already enrolled
    public void enrollStudent(String studentId, ReadWriteFile rwf) throws IOException {
        if (!enrolledStudents.contains(studentId)) {
            enrolledStudents.add(studentId);
            rwf.updateEnrollments(this.sectionCode, this.course, studentId); 
        } else {
            System.out.println("Student already enrolled in this section.");
        }
    }


    public String getSectionCode() {
        return sectionCode;
    }

    public String getFaculty() {
        return faculty;
    }

    public List<newMeetingSession> getMeetingSessions() {
        return meetingSessions;
    }

    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }


  

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setMeetingSessions(List<newMeetingSession> meetingSessions) {
        this.meetingSessions = meetingSessions;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

  
    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    //check if this section conficts with other sections
    public boolean hasTimeConflict(newMeetingSession newMeetingSession) {
        return meetingSessions.stream().anyMatch(existingSession -> existingSession.hasOverlap(newMeetingSession));
    }
    
    public boolean hasTimeConflict(List<newMeetingSession> newMeetingSessions) {
        return newMeetingSessions.stream().anyMatch(this::hasTimeConflict);
    }
    
}

