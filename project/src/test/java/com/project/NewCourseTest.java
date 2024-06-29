package com.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

public class NewCourseTest {

    private newCourse course;
    private ReadWriteFile mockReadWriteFile;

    @BeforeEach
    public void setUp() throws Exception {
        List<String> prerequisites = Arrays.asList("PreReq1", "PreReq2");
        course = new newCourse("CS101", "Intro to CS", 30, 3, prerequisites);

        mockReadWriteFile = Mockito.mock(ReadWriteFile.class);
       
    }

    @Test
   public void testAddAndRemoveSection() {
        course.addSection("S1");
        assertTrue(course.getSections().contains("S1"));
        course.removeSection("S1");
        assertFalse(course.getSections().contains("S1"));
    }

  




    @Test
    public void testIsFull() {
        for (int i = 0; i < course.getMaxCapacity(); i++) {
            course.enrollStudent("student" + i);
        }
        assertTrue(course.isFull());
    }

    @Test
    public void testGetAvailableSeats() {
        int initialAvailableSeats = course.getAvailableSeats();
        course.enrollStudent("12345");
        assertEquals(initialAvailableSeats - 1, course.getAvailableSeats());
    }

    
}
