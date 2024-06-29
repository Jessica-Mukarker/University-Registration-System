package com.project;
import java.io.IOException;
import java.time.LocalDate;

public class GradingMain {
    public static void main(String[] args) {
        try {
            AcademicRecordSystem academicRecordSystem = new AcademicRecordSystem();

            Major biologyMajor = new Major("Biology");

            newStudent alice = new newStudent("Alice", "1001", "555-0101", "alice123", LocalDate.of(2000, 1, 1), biologyMajor);

            academicRecordSystem.addGrade("1001", "Fall 2023", "BIO101", "A", 4);
            academicRecordSystem.addGrade("1001", "Fall 2023", "CHEM101", "B", 3);

            String transcript = academicRecordSystem.generateTranscript("1001");
            System.out.println("Transcript for Alice:\n" + transcript);

            
            double gpaWithTask = academicRecordSystem.calculateGPAWithTask("1001");
            System.out.println("Alice's GPA calculated with GPACalculationTask: " + gpaWithTask);

           System.out.println(academicRecordSystem.calculateAcademicStanding("1001")); 

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

