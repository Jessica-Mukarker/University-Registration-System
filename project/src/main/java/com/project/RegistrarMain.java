package com.project;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class RegistrarMain {
    public static void main(String[] args) {
        newRegistrar registrar = new newRegistrar();

        try {
            registrar.createNewCourse("CS202", "Data Structures", 4, 40, Arrays.asList("CS101"));
        } catch (IOException e) {
            System.out.println("Error creating course: " + e.getMessage());
        }

        try {
            registrar.createFaculty("Dr. Alice", "3001", "555-0301", "alicepwd", "Computer Science", LocalDate.parse("1978-04-01"));
        } catch (IOException e) {
            System.out.println("Error creating faculty: " + e.getMessage());
        }

        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            newMeetingSession session1 = new newMeetingSession("Mon", LocalTime.parse("09:00", timeFormatter), LocalTime.parse("11:00", timeFormatter), "Room 202");
            newMeetingSession session2 = new newMeetingSession("Wed", LocalTime.parse("09:00", timeFormatter), LocalTime.parse("11:00", timeFormatter), "Room 202");
            registrar.createSection("SEC501", "Dr. Alice", "Data Structures", Arrays.asList(session1, session2));
        } catch (IOException e) {
            System.out.println("Error creating section: " + e.getMessage());
        }

        try {
            registrar.createStudent("Bob", "1004", "555-0104", "bobpwd", "Software Engineering", LocalDate.parse("2002-05-15", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } catch (IOException e) {
            System.out.println("Error creating student: " + e.getMessage());
        }
         try {
            newStudent student = registrar.getStudentById("1003");
            if (student != null) {
                System.out.println("Student found: " + student.getName());
            } else {
                System.out.println("Student not found.");
            }
        } catch (IOException e) {
            System.out.println("Error finding student: " + e.getMessage());
        }

        try {
            Major major = registrar.getMajorByName("Software Engineering");
            if (major != null) {
                System.out.println("Major found: " + major.getMajor());
            } else {
                System.out.println("Major not found.");
            }
        } catch (IOException e) {
            System.out.println("Error finding major: " + e.getMessage());
        }

        try {
            newCourse course = registrar.getCourseByCode("CS202");
            if (course != null) {
                System.out.println("Course found: " + course.getCourseName());
            } else {
                System.out.println("Course not found.");
            }
        } catch (IOException e) {
            System.out.println("Error finding course: " + e.getMessage());
        }

        try {
            List<newSection> sections = registrar.getSectionsByCourse("CS202");
            if (!sections.isEmpty()) {
                System.out.println("Sections found for course CS202:");
                for (newSection section : sections) {
                    System.out.println("Section: " + section.getSectionCode());
                }
            } else {
                System.out.println("No sections found for course CS202.");
            }
        } catch (IOException e) {
            System.out.println("Error finding sections: " + e.getMessage());
        }
    }
}