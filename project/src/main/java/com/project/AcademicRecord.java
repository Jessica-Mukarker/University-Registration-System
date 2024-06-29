package com.project;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class AcademicRecord {
    private String studentId;
    private String semester;
    private String courseCode;
    private String grade;
    private int creditHours;

    public AcademicRecord(String studentId, String semester, String courseCode, String grade, int creditHours) {
        this.studentId = studentId;
        this.semester = semester;
        this.courseCode = courseCode;
        this.grade = grade;
        this.creditHours = creditHours;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

   // Calculates the cumulative GPA of a student based on their academic history
    public double calculateCumulativeGPA(newStudent student) {
        double totalGradePoints = 0.0;
        int totalCredits = 0;

        for (Map.Entry<Semester, Map<newCourse, Grade>> entry : student.getAcademicHistory().entrySet()) {
            Map<newCourse, Grade> grades = entry.getValue();
            for (Map.Entry<newCourse, Grade> gradeEntry : grades.entrySet()) {
                Grade grade = gradeEntry.getValue();
                int credits = gradeEntry.getKey().getCredits();
                totalGradePoints += grade.getGPAValue() * credits;
                totalCredits += credits;
            }
        }

        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }

    // Determines the academic standing of a student based on their GPA
    public String determineAcademicStanding(newStudent student) {
        double gpa = calculateCumulativeGPA(student);
        if (gpa < 2.0) {
            return "Probation";
        } else {
            return "Good Standing";
        }
    }

    // Inner class to perform GPA calculation in a multi-threaded manner
    public static class GPACalculationTask extends RecursiveTask<Double> {
        private List<Grade> grades;
        private int start;
        private int end;
        private static final int THRESHOLD = 10;

        public GPACalculationTask(List<Grade> grades, int start, int end) {
            this.grades = grades;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Double compute() {
            if ((end - start) <= THRESHOLD) {
                return calculateGradePoints();
            } else {
                int mid = start + (end - start) / 2;
                GPACalculationTask left = new GPACalculationTask(grades, start, mid);
                GPACalculationTask right = new GPACalculationTask(grades, mid, end);

                left.fork();
                double rightResult = right.compute();
                double leftResult = left.join();

                return leftResult + rightResult;
            }
        }

        private double calculateGradePoints() {
            double total = 0;
            for (int i = start; i < end; i++) {
                Grade grade = grades.get(i);
                total += grade.getGPAValue() * grade.getCredits();
            }
            return total;
        }
    }

    // Calculates the GPA of a student based on a list of their grades
    public double calculateGPA(List<Grade> studentGrades) {
        GPACalculationTask task = new GPACalculationTask(studentGrades, 0, studentGrades.size());

        try (ForkJoinPool pool = new ForkJoinPool()) {
            double totalGradePoints = pool.invoke(task);

            int totalCredits = studentGrades.stream().mapToInt(Grade::getCredits).sum();
            return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
        }
    }

}