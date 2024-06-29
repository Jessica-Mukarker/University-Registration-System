package com.project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class newStudentTest {

    private newStudent student;
    private newCourse course;
    private Grade grade;
    private Major major;
    private Faculty advisor;
    private newMeetingSession meetingSession;
    private Set<newCourse> courses;

    @BeforeEach
    public void setUp() {
        major = new Major("Computer Science");
        LocalDate dob = LocalDate.of(2024, 1, 1); 
        advisor = new Faculty("Dr. Smith", "facultyID", "drsmith@university.com", "password123", "Computer Science", dob);
        List<String> prerequisites = Arrays.asList("CS100"); 
        course = new newCourse("CS101", "Introduction to Programming", 30, 3, prerequisites);
        grade = new Grade("CS101", "B+", 3);
        LocalTime startTime = LocalTime.of(10, 0); 
        LocalTime endTime = LocalTime.of(12, 0); 
        meetingSession = new newMeetingSession("Monday", startTime, endTime, "Room 101");
        courses = new HashSet<>();
        courses.add(course);

        student = new newStudent("John Doe", "1234567", "johndoe@example.com", LocalDate.of(2000, 1, 1), "password123", major, 1, new HashSet<>(), new HashSet<>(), courses, "Fall 2024", advisor);
    }



    @Test
    public void testSetters() {
        student.setName("Jane Doe");
        assertEquals("Jane Doe", student.getName());

        student.setId("654321");
        assertEquals("654321", student.getId());

    }

   

    @Test
    public void testCalculateGPA() {
        student.addGrade(course, grade);
        assertEquals(3.0, student.calculateGPA(), 0.01);
    }

    
    @Test
    public void testDropCourse() {
        List<String> pre = Arrays.asList("CS100"); 
        newCourse newCourse = new newCourse("CS101", "Introduction to Programming", 30, 3, pre);        student.enrollInCourse(newCourse);
        student.dropCourse(newCourse);
        assertFalse(student.isEnrolledInCourse(newCourse));
    }

    
    @Test
    public void testDoesNotMeetPrerequisites() {
        newCourse prerequisiteCourse = new newCourse("CS50", "Intro to Programming", 30,3,null);
        newCourse advancedCourse = new newCourse("CS200", "Advanced Programming", 30,3,  new ArrayList<>(Arrays.asList("CS100")));

        student.setCoursesFinished(new HashSet<>());

        assertFalse(student.meetsPrerequisites(advancedCourse));
    }


    @Test
    public void testHasScheduleConflict() {

        newCourse courseWithConflict = new newCourse("CS103", "Database Systems", 30,3,null);
        List<newMeetingSession> meetingSessions = Arrays.asList(meetingSession); // Assuming same meeting session as an existing course

        student.addClassToSchedule(course, Arrays.asList(meetingSession));
        assertTrue(student.hasScheduleConflict(courseWithConflict, meetingSessions));
    }

    @Test
    public void testGetClassesAt() {
        student.addClassToSchedule(course, Arrays.asList(meetingSession));
        List<ScheduledCourse> classesAt = student.getClassesAt(meetingSession);
        assertEquals(1, classesAt.size());
        assertEquals(course, classesAt.get(0).getCourse());
    }

    
}
