package com.project;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Schedule {
    private List<ScheduledCourse> scheduledCourses;

    public Schedule() {
        this.scheduledCourses = new ArrayList<>();
    }

    public void addCourse(ScheduledCourse course) {
        scheduledCourses.add(course);
    }

    public void removeCourse(ScheduledCourse course) {
        scheduledCourses.remove(course);
    }

    public List<ScheduledCourse> getCoursesAt(newMeetingSession meetingSession) {
        return scheduledCourses.stream()
                .filter(course -> course.getMeetingSessions().contains(meetingSession))
                .collect(Collectors.toList());
    }

    //get courses at these times
    public List<ScheduledCourse> getCoursesAt(List<newMeetingSession> meetingSessions) {
        List<ScheduledCourse> coursesAtTime = new ArrayList<>();
        for (newMeetingSession session : meetingSessions) {
        
            coursesAtTime.addAll(findCoursesAtSingleSession(session));
        }
        return coursesAtTime;
    }
    
    //get courses at a specific time
    public List<ScheduledCourse> findCoursesAtSingleSession(newMeetingSession targetSession) {
        List<ScheduledCourse> coursesAtTime = new ArrayList<>();
        for (ScheduledCourse scheduledCourse : scheduledCourses) {
            for (newMeetingSession session : scheduledCourse.getMeetingSessions()) {
                if (session.hasOverlap(targetSession)) {
                    coursesAtTime.add(scheduledCourse);
                    break; 
                }
            }
        }
        return coursesAtTime;
    }
    
}
