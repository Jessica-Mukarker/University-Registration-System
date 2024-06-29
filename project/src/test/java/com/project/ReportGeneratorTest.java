package com.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

// Other necessary imports...


public class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private List<newCourse> mockCourses;
    private List<Faculty> mockFaculties;
    private List<newStudent> mockStudents;

    @BeforeEach
    public void setUp() {
        reportGenerator = new ReportGenerator();

        // Mock data for newCourse
        newCourse course1 = new newCourse("CS101", "Introduction to Java", 30, 3, Arrays.asList("CS100"));
        newCourse course2 = new newCourse("CS201", "Advanced Java", 25, 4, Arrays.asList("CS101"));

        course1.setEnrolledStudents(Arrays.asList("101", "102", "103"));
        course2.setEnrolledStudents(Arrays.asList("201", "202"));

        // Mock data for Faculty
        LocalDate faculty1Dob = LocalDate.of(1975, 1, 1); // Example date of birth
        LocalDate faculty2Dob = LocalDate.of(1980, 1, 1); // Example date of birth

        Faculty faculty1 = new Faculty("Dr. Smith", "F001", "smith@example.com", "password1", "Computer Science",
                faculty1Dob);
        Faculty faculty2 = new Faculty("Dr. Johnson", "F002", "johnson@example.com", "password2", "Computer Science",
                faculty2Dob);

        faculty1.setCourses(Arrays.asList(course1));
        faculty2.setCourses(Arrays.asList(course2));

        mockFaculties = Arrays.asList(faculty1, faculty2);

        // Mock data for newStudent
        newStudent student1 = new newStudent("Alice", "101", "contact1", "password1",LocalDate.of(2000, 1, 1),
                new Major("Major1"));
        newStudent student2 = new newStudent("Bob", "102", "contact2", "password2",LocalDate.of(2000, 2, 2),
                new Major("Major2"));

        // Convert List to Set before setting the courses
        Set<newCourse> coursesForStudent1 = new HashSet<>(Arrays.asList(course1));
        Set<newCourse> coursesForStudent2 = new HashSet<>(Arrays.asList(course2));

        student1.setCoursesToTake(coursesForStudent1);
        student2.setCoursesToTake(coursesForStudent2);

        mockStudents = Arrays.asList(student1, student2);
    }

   

    @Test
    void testGenerateFacultyLoadReport() {
        String report = reportGenerator.generateFacultyLoadReport(mockFaculties);
        assertNotNull(report);
        // Add more assertions here to validate the content of the report
    }

   
}
