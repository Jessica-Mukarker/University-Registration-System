package com.project;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StudentScheduleManager {
    private ReadWriteFile rwf;

    public StudentScheduleManager() {
        this.rwf = new ReadWriteFile();
    }

    
   

    public Optional<newStudent> findStudentById(String studentId) throws IOException {
        List<String> studentFileLines = rwf.readFromFile("project\\project\\resources\\students.txt");
        for (String line : studentFileLines) {
            newStudent student = parseStudentLine(line);
            if (student != null && student.getId() != null) {
                System.out.println("Checking student ID: " + student.getId()); // Debugging statement
                if (student.getId().equals(studentId)) {
                    return Optional.of(student);
                }
            }
        }
        return Optional.empty();
    }
    private newStudent parseStudentLine(String line) {
        String[] parts = line.split(" ");
        if (parts.length >= 6) {
            String name = parts[0];
            String id = parts[1];
            String contactDetails = parts[2];
            String password = parts[3];
            String majorName = parts[4];
            LocalDate dateOfBirth = LocalDate.parse(parts[5], DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    
            System.out.println("Name: " + name);
            System.out.println("ID: " + id);
            System.out.println("Contact Details: " + contactDetails);
            System.out.println("Password: " + password);
            System.out.println("Major Name: " + majorName);
            System.out.println("Date of Birth: " + dateOfBirth);
    
            Major major = new Major(majorName); 
            return new newStudent(name, id, contactDetails, password, dateOfBirth, major);
        } else {
            System.out.println("Invalid line format: " + line);
        }
        return null;
    }
   

private newSection parseSectionLine(String line) {
    String[] parts = line.split(",", 4);
    if (parts.length >= 4) {
        String sectionCode = parts[0].trim();
        String facultyName = parts[1].trim();
        String courseName = parts[2].trim();
        String meetingSessionsStr = parts[3].trim();
        List<newMeetingSession> meetingSessions = parseMeetingSessions(meetingSessionsStr);

        return new newSection(sectionCode, courseName, facultyName, meetingSessions);
    }
    return null;
}


//to create a scheduale for this student
 public Map<String, List<String>> generateStudentSchedule(String studentId) throws IOException {
    ReadWriteFile rwf=new ReadWriteFile();
        Map<String, List<String>> schedule = new HashMap<>();

        Set<String> enrolledCourses = rwf.getEnrolledCourses(studentId);

        Map<String, List<String>> courseSections = rwf.readCourseSections();

        Map<String, String> sectionDetails = readSectionsDetails();

        for (String course : enrolledCourses) {
            List<String> sections = courseSections.get(course);
            if (sections != null) {
                List<String> courseSchedule = new ArrayList<>();
                for (String section : sections) {
                    String details = sectionDetails.get(section);
                    if (details != null) {
                        courseSchedule.add(details);
                    }
                }
                schedule.put(course, courseSchedule);
            }
        }
        return schedule;
    }


    


    private void appendCourseToStudentFile(String studentId, String courseCode) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("project\\project\\resources\\student_courses.txt", true))) {
            writer.write(studentId + " " + courseCode);
            writer.newLine();
        }
    }
    private Map<String, String> readSectionsDetails() throws IOException {
        ReadWriteFile rwf=new ReadWriteFile();
        Map<String, String> sectionDetails = new HashMap<>();
        List<String> lines = rwf.readSectionFile();
        for (String line : lines) {
            String[] parts = line.split(",", 4);
            if (parts.length >= 4) {
                String sectionCode = parts[0].trim();
                String details = line.substring(line.indexOf(',') + 1).trim(); 
                sectionDetails.put(sectionCode, details);
            }
        }
        return sectionDetails;
    }


  

private List<newMeetingSession> parseMeetingSessions(String meetingSessionsStr) {
    List<newMeetingSession> meetingSessions = new ArrayList<>();
    String[] sessions = meetingSessionsStr.split(";");
    for (String session : sessions) {
        String[] sessionParts = session.trim().split(" ");
        if (sessionParts.length >= 3) {
            String day = sessionParts[0];
            String[] times = sessionParts[1].split("-");
            LocalTime startTime = LocalTime.parse(times[0]);
            LocalTime endTime = LocalTime.parse(times[1]);
            String room = sessionParts[2];
            meetingSessions.add(new newMeetingSession(day, startTime, endTime, room));
        }
    }
    return meetingSessions;
}

public boolean addCourse(String studentId, String courseCode) throws IOException {
    Optional<newStudent> studentOpt = findStudentById(studentId);
    if (!studentOpt.isPresent()) {
        System.out.println("Student not found.");
        return false;
    }

    Map<String, List<String>> studentSchedule = generateStudentSchedule(studentId);
    Map<String, List<String>> courseSections = rwf.readCourseSections();
    Map<String, String> sectionDetails = readSectionsDetails();

    List<String> sectionsForCourse = courseSections.get(courseCode);
    if (sectionsForCourse == null || sectionsForCourse.isEmpty()) {
        System.out.println("No sections available for this course.");
        return false;
    }

    for (String section : sectionsForCourse) {
        String sectionDetail = sectionDetails.get(section);
        newSection sectionObj = parseSectionLine(sectionDetail);
        if (sectionObj != null) {
            if (!hasTimeConflict(studentSchedule, sectionObj)) {
                appendCourseToStudentFile(studentId, courseCode);
                System.out.println("Course added successfully.");
                return true;
            }
        }
    }
    System.out.println("Scheduling conflict detected. Course not added.");
    return false;
}

private boolean hasTimeConflict(Map<String, List<String>> studentSchedule, newSection sectionObj) {
    for (List<String> courseDetails : studentSchedule.values()) {
        for (String detail : courseDetails) {
            newSection existingSection = parseSectionLine(detail);
            if (existingSection != null) {
                for (newMeetingSession existingSession : existingSection.getMeetingSessions()) {
                    for (newMeetingSession newSession : sectionObj.getMeetingSessions()) {
                        if (existingSession.getDay().equals(newSession.getDay()) &&
                            existingSession.getStartTime().isBefore(newSession.getEndTime()) &&
                            newSession.getStartTime().isBefore(existingSession.getEndTime())) {
                            return true;
                        }
                    }
                }
            }
        }
    }
    return false;
}

    private newCourse parseCourseLine(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            String courseCode = parts[0].trim();
            String courseName = parts[1].trim();
            int credits = Integer.parseInt(parts[2].trim());
            int maxCapacity = Integer.parseInt(parts[3].trim());
            List<String> prerequisites = Arrays.asList(parts[4].split(";")).stream().map(String::trim).collect(Collectors.toList());
            return new newCourse(courseCode, courseName, maxCapacity, credits, prerequisites);
        }
        return null;
    }

   
    public static class ClassDetails {
        private String courseName;
        private String sectionCode;
        private newMeetingSession session;

        public ClassDetails(String courseName, String sectionCode, newMeetingSession session) {
            this.courseName = courseName;
            this.sectionCode = sectionCode;
            this.session = session;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getSectionCode() {
            return sectionCode;
        }

        public void setSectionCode(String sectionCode) {
            this.sectionCode = sectionCode;
        }

        public newMeetingSession getSession() {
            return session;
        }

        public void setSession(newMeetingSession session) {
            this.session = session;
        }

        

    }
}
