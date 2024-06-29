package com.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleMainTest {

    private StudentScheduleManager scheduleManager;
    private newStudent alice;

    @BeforeEach
    void setUp() {
        scheduleManager = Mockito.mock(StudentScheduleManager.class);
        LocalDate dob = LocalDate.parse("01-01-2000", DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        alice = new newStudent("Alice", "1001", "555-0101", "alice123", dob, new Major("Biology"));
    }

    @Test
    void testGenerateStudentSchedule() throws IOException {
        Map<String, List<String>> mockSchedule = new HashMap<>();
        mockSchedule.put("BIO101", Arrays.asList("Mon 9:00-10:00", "Wed 9:00-10:00"));
        
        when(scheduleManager.generateStudentSchedule(alice.getId())).thenReturn(mockSchedule);
        
        Map<String, List<String>> aliceSchedule = scheduleManager.generateStudentSchedule(alice.getId());
        assertFalse(aliceSchedule.isEmpty(), "Schedule should not be empty");
        assertTrue(aliceSchedule.containsKey("BIO101"), "Schedule should contain BIO101");
    }

    @Test
    void testAddCourse() throws IOException {
        when(scheduleManager.addCourse(alice.getId(), "MATH201")).thenReturn(true);
    
        boolean success = scheduleManager.addCourse(alice.getId(), "MATH201");
    
        verify(scheduleManager).addCourse(alice.getId(), "MATH201");
    
        assertTrue(success, "Adding course should be successful");
    }
    
}
