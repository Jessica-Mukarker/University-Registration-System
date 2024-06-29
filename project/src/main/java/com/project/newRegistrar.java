package com.project;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class newRegistrar {

    private ReadWriteFile rwf;

    public newRegistrar() {
        rwf = new ReadWriteFile();
    }

    //a method to create a new Course
    public void createNewCourse(String courseCode, String courseName, int credits, int maxCapacity, List<String> prerequisites) throws IOException {
        List<String> courses = rwf.readCourseFile();
        boolean courseExists = courses.stream().anyMatch(line -> line.startsWith(courseCode + ","));
        if (!courseExists) {
            newCourse course = new newCourse(courseCode, courseName, maxCapacity, credits, prerequisites);
            rwf.addToCourseFile(course);
            System.out.println("New course created successfully.");
        } else {
            System.out.println("Course with code " + courseCode + " already exists.");
        }
    }
    
    //a method to create a new fuculty
    public void createFaculty(String name, String id, String contactDetails, String password, String majorName, LocalDate dateOfBirth) throws IOException {
        List<String> majors = rwf.readMajorFile();
        boolean majorExists = majors.stream().anyMatch(line -> line.contains(majorName));

        if (!majorExists) {
            System.out.println("Major " + majorName + " does not exist. Faculty cannot be created.");
            return;
        }

        List<String> faculties = rwf.readFaculty();
        boolean facultyExists = faculties.stream().anyMatch(line -> line.contains(" " + id + " "));
        if (facultyExists) {
            System.out.println("Faculty with ID " + id + " already exists.");
            return;
        }

        Faculty faculty = new Faculty(name, id, contactDetails, password, majorName, dateOfBirth);
        rwf.addToFacultyFile(faculty);
        System.out.println("New faculty member created successfully.");
    }


    
    //a method to create a new section in a course
    public void createSection(String sectionCode, String facultyName, String courseName, List<newMeetingSession> meetingSessions) throws IOException {
        List<String> sections = rwf.readSectionFile();
        boolean sectionExists = sections.stream().anyMatch(line -> line.startsWith(sectionCode + ","));
        if (!sectionExists) {
            newSection section = new newSection(sectionCode, facultyName, courseName, meetingSessions);
            rwf.addOrUpdateSection(section);
            System.out.println("New section created successfully.");
        } else {
            System.out.println("Section with code " + sectionCode + " already exists.");
        }
    }
    
    
    //a method to create a new student
    public void createStudent(String name, String id, String contactDetails, String password, String majorName, LocalDate dateOfBirth) throws IOException {
        List<String> students = rwf.readStudentFile();
        boolean studentExists = students.stream().anyMatch(line -> line.contains("," + id + ","));
        if (!studentExists) {
            Major major = rwf.getMajorByName(majorName);
            if (major != null) {
                Major newMajor= new Major(majorName);
                newStudent student = new newStudent(name, id, contactDetails, password, newMajor,dateOfBirth);
                rwf.addToStudentFile(student);
                System.out.println("New student created successfully.");
            } else {
                System.out.println("Major not found. Student not created.");
            }
        } else {
            System.out.println("Student with ID " + id + " already exists.");
        }
    }
    

    //reads the students file and returns them as a student objects by their ids
    public newStudent getStudentById(String studentId) throws IOException {
        List<String> students = rwf.readStudentFile();
        for (String line : students) {
            newStudent student = rwf.parseStudentLine(line);
            if (student != null && student.getId().equals(studentId)) {
                return student;
            }
        }
        return null; 
    }

    //return major by its name
    public Major getMajorByName(String majorName) throws IOException {
    Map<String, List<String>> majors = rwf.readMajorsWithCourses();
    if (majors.containsKey(majorName)) {
        List<String> courses = majors.get(majorName);
        return new Major(majorName, courses); 
    }
    return null; 
}

//a method to return a the course with that specific code
public newCourse getCourseByCode(String courseCode) throws IOException {
    List<String> courses = rwf.readCourseFile();
    for (String line : courses) {
        newCourse course = rwf.parseCourseLine(line);
        if (course != null && course.getCourseCode().equals(courseCode)) {
            return course;
        }
    }
    return null; 
}

//return all sections in that sepcific course
public List<newSection> getSectionsByCourse(String courseCode) throws IOException {
    List<newSection> sectionsForCourse = new ArrayList<>();
    Map<String, String> sectionDetails = rwf.readSectionsDetails();

    for (String sectionCode : sectionDetails.keySet()) {
        if (sectionCode.startsWith(courseCode)) {
            String details = sectionDetails.get(sectionCode);
            newSection section = rwf.parseSectionLine(details);
            if (section != null) {
                sectionsForCourse.add(section);
            }
        }
    }
    return sectionsForCourse;
}

    
}

