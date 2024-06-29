package com.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class NewSectionTest {

    @Mock
    private ReadWriteFile rwfMock;

    private newSection section;
    private List<newMeetingSession> meetingSessions;

    @BeforeEach
    void setUp() {
        newMeetingSession session1 = mock(newMeetingSession.class);
        newMeetingSession session2 = mock(newMeetingSession.class);
        meetingSessions = new ArrayList<>(Arrays.asList(session1, session2));

        section = new newSection("A", "CS101", "Dr. Smith", meetingSessions);
    }


    @Test
    void testEnrollStudent() throws IOException {
        String studentId = "12345";
        section.enrollStudent(studentId, rwfMock);
        assertTrue(section.getEnrolledStudents().contains(studentId));
        verify(rwfMock).updateEnrollments(section.getSectionCode(), section.getCourse(), studentId);
    }

    @Test
    void testEnrollAlreadyEnrolledStudent() throws IOException {
        String studentId = "12345";
        section.enrollStudent(studentId, rwfMock); 
        section.enrollStudent(studentId, rwfMock); 

        assertEquals(1, section.getEnrolledStudents().stream().filter(id -> id.equals(studentId)).count());
        verify(rwfMock, times(1)).updateEnrollments(section.getSectionCode(), section.getCourse(), studentId);
    }


}
