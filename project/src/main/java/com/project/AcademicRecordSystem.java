package com.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import com.project.AcademicRecord.GPACalculationTask;

public class AcademicRecordSystem {
    private ReadWriteFile rwf;
    private Map<String, List<AcademicRecord>> studentRecords;
    private Map<String, List<Grade>> studentGrades;

    public AcademicRecordSystem() throws IOException {
        rwf = new ReadWriteFile();
        studentRecords = new HashMap<>();
        this.studentGrades = new ConcurrentHashMap<>();
        loadRecords();
    }

    public AcademicRecordSystem(ReadWriteFile mockReadWriteFile) {
        this.rwf=mockReadWriteFile;
        studentRecords = new HashMap<>();
        this.studentGrades = new ConcurrentHashMap<>();
    }
    

    // Method to load academic records from a file
    public void loadRecords() throws IOException {
        List<String> records = rwf.readAcademicRecordFile();
        for (String record : records) {
            String[] parts = record.trim().split("\\s+");
            if (parts.length != 6) { 
                System.err.println("Invalid record format: " + record);
                continue;
            }
    
            String studentId = parts[0];
            String semester = parts[1] + " " + parts[2];
            String courseCode = parts[3];
            String letterGrade = parts[4];
            int creditHours = Integer.parseInt(parts[5]);
    
            AcademicRecord ar = new AcademicRecord(studentId, semester, courseCode, letterGrade, creditHours);
            Grade newGrade = new Grade(courseCode, letterGrade, creditHours);
    
            studentRecords.computeIfAbsent(studentId, k -> new ArrayList<>()).add(ar);
            studentGrades.computeIfAbsent(studentId, k -> new ArrayList<>()).add(newGrade);
        }
    }
    
    // Method to add a new grade for a student
    public void addGrade(String studentId, String semester, String courseCode, String letterGrade, int creditHours)
            throws IOException {
        AcademicRecord ar = new AcademicRecord(studentId, semester, courseCode, letterGrade, creditHours);
        studentRecords.computeIfAbsent(studentId, k -> new ArrayList<>()).add(ar);
        Grade newGrade = new Grade(courseCode, letterGrade, creditHours);
        studentGrades.computeIfAbsent(studentId, k -> new ArrayList<>()).add(newGrade);
        rwf.addToAcademicRecordFile(studentId, semester, courseCode, letterGrade, creditHours);
    }

    // Utility method to convert a letter grade to a numerical grade
    private double convertLetterToNumericalGrade(String letterGrade) {
        switch (letterGrade) {
            case "A":
                return 90;
            case "B":
                return 80;
            case "C":
                return 70;
            case "D":
                return 60;
            default:
                return 0;
        }
    }
  
    // Method to calculate the total grade points of a student
    public double calculateTotalGradePoints(String studentId) {
        List<Grade> grades = studentGrades.getOrDefault(studentId, new ArrayList<>());
        return grades.parallelStream()
                .mapToDouble(grade -> grade.getGPAValue() * grade.getCredits())
                .sum();
    }

    // Method to generate a transcript for a student
    public String generateTranscript(String studentId) {
        StringBuilder transcript = new StringBuilder();
        List<AcademicRecord> records = studentRecords.getOrDefault(studentId, new ArrayList<>());
        for (AcademicRecord ar : records) {
            transcript.append(ar.getCourseCode())
                    .append(" - ")
                    .append(ar.getGrade())
                    .append(" - ")
                    .append(ar.getCreditHours())
                    .append(" Credits\n");
        }
        transcript.append("GPA: ").append(calculateGPA(studentId));
        return transcript.toString();
    }

    // Method to calculate the GPA of a student
    public double calculateGPA(String studentId) {
        List<Grade> grades = studentGrades.getOrDefault(studentId, new ArrayList<>());
    
        if (grades.isEmpty()) {
            return 0.0;
        }
    
        GPACalculationTask task = new GPACalculationTask(grades, 0, grades.size());
        ForkJoinPool pool = new ForkJoinPool();
        double totalGradePoints = pool.invoke(task);
    
        int totalCredits = grades.stream().mapToInt(Grade::getCredits).sum();
    
        pool.shutdown();
        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }
    
    // Method to calculate the academic standing of a student based on GPA
    public String calculateAcademicStanding(String studentId) {
        double gpa = calculateGPA(studentId);
        if (gpa >= 3.5) {
            return "Dean's List";
        } else if (gpa >= 2.0) {
            return "Good Standing";
        } else {
            return "Probation";
        }
    }

    //Calculate the gpa using threds
     public double calculateGPAWithTask(String studentId) {
        List<Grade> grades = studentGrades.getOrDefault(studentId, new ArrayList<>());
        if (grades.isEmpty()) {
            return 0.0;
        }

        GPACalculationTask task = new GPACalculationTask(grades, 0, grades.size());
        ForkJoinPool pool = new ForkJoinPool();
        double totalGradePoints = pool.invoke(task);

        int totalCredits = grades.stream().mapToInt(Grade::getCredits).sum();

        pool.shutdown();
        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }

}
