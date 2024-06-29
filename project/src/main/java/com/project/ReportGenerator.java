package com.project;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ReportGenerator {

    public String generateCourseEnrollmentReport(List<newCourse> courses) {
        StringBuilder report = new StringBuilder();
        report.append("Course Enrollment Report\n");
        courses.forEach(course -> 
            report.append(course.getCourseName())
                  .append(" - ")
                  .append(course.getEnrolledStudentIds().size())
                  .append(" students enrolled\n"));
        return report.toString();
    }

    public String generateFacultyLoadReport(List<Faculty> faculties) {
        StringBuilder report = new StringBuilder();
        report.append("Faculty Load Report\n");
        faculties.forEach(faculty -> {
            report.append(faculty.getName())
                  .append(" teaches:\n");
            faculty.getCourses().forEach(course ->
                report.append("\t").append(course.getCourseName()).append("\n"));
        });
        return report.toString();
    }

    public String generateStudentPerformanceReport(List<newStudent> students) {
        StringBuilder report = new StringBuilder();
        report.append("Student Performance Report\n");
        students.forEach(student -> {
            report.append(student.getName())
                  .append(" - GPA: ")
                  .append(student.calculateGPA())
                  .append("\n");
        });
        return report.toString();
    }


     public CompletableFuture<String> generateCourseEnrollmentReportAsync(List<newCourse> courses) {
        return CompletableFuture.supplyAsync(() -> 
            courses.parallelStream()
                   .map(course -> course.getCourseName() + " - " + course.getEnrolledStudentIds().size() + " students enrolled\n")
                   .collect(Collectors.joining("", "Course Enrollment Report\n", ""))
        );
    }
    
}
