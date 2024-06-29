package com.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AcademicRecordSystemTest {

    private AcademicRecordSystem academicRecordSystem;
    private ReadWriteFile mockReadWriteFile;

    @BeforeEach
    void setUp() throws IOException {
        mockReadWriteFile = Mockito.mock(ReadWriteFile.class);
        when(mockReadWriteFile.readAcademicRecordFile()).thenReturn(List.of("1001 Fall 2023 BIO101 A 4"));
        academicRecordSystem = new AcademicRecordSystem(mockReadWriteFile);
        academicRecordSystem.addGrade("1001", "Fall 2023", "CHEM101", "B", 3); // Sufficiently high grade
    }

    @Test
    void testAddGradeAndGenerateTranscript() throws IOException {
        Major biologyMajor = new Major("Biology");
        newStudent alice = new newStudent("Alice", "1001", "555-0101", "alice123", LocalDate.of(2000, 1, 1), biologyMajor);

        academicRecordSystem.addGrade("1001", "Fall 2023", "BIO101", "A", 4);
        academicRecordSystem.addGrade("1001", "Fall 2023", "CHEM101", "B", 3);

        String transcript = academicRecordSystem.generateTranscript("1001");
        assertTrue(transcript.contains("BIO101 - A - 4 Credits"));
        assertTrue(transcript.contains("CHEM101 - B - 3 Credits"));
    }

    @Test
    void testCalculateGPAWithTask() {
        double expectedGPA = 3.0; 
        double gpaWithTask = academicRecordSystem.calculateGPAWithTask("1001");
        assertEquals(expectedGPA, gpaWithTask, 0.01);
    }

    @Test
    void testCalculateAcademicStanding() {
        String expectedStanding = "Good Standing"; 
        String standing = academicRecordSystem.calculateAcademicStanding("1001");
        assertEquals(expectedStanding, standing);
    }
}
