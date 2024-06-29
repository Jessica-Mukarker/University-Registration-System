package com.project;

import java.util.List;

public class ScheduledCourse {
    private newCourse course;
    private List<newMeetingSession> meetingSessions;

    public ScheduledCourse(newCourse course, List<newMeetingSession> meetingSessions) {
        this.course = course;
        this.meetingSessions = meetingSessions;
    }

    public newCourse getCourse() {
        return course;
    }

    public void setCourse(newCourse course) {
        this.course = course;
    }

    public List<newMeetingSession> getMeetingSessions() {
        return meetingSessions;
    }

    public void setMeetingSessions(List<newMeetingSession> meetingSessions) {
        this.meetingSessions = meetingSessions;
    }

}
