package com.project;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class ReadWriteFile {
    private BufferedReader studentReader;
    private BufferedReader staffReader;
    private BufferedReader facultyReader;
    private BufferedReader courseReader;
    private BufferedReader majorReader;

    private BufferedWriter studentWriter;
    private BufferedWriter staffWriter;
    private BufferedWriter facultyWriter;
    private BufferedWriter courseWriter;
    private BufferedWriter majorWriter;

    private static final String STUDENT_FILE_NAME = "project\\project\\resources\\students.txt";
    private static final String STAFF_FILE_NAME = "Staff.txt";
    private static final String COURSE_FILE_NAME = "project\\project\\resources\\courses.txt";
    private static final String MAJOR_FILE_NAME = "project\\project\\resources\\Majors.txt";
    private static final String FACULTY_FILE_NAME = "project\\project\\resources\\faculties.txt";
    private static final String ACADEMIC_RECORD_FILE_NAME = "project\\project\\resources\\academic_records.txt";
    private static final String ENROLLMENT_FILE_NAME = "project\\project\\resources\\enrollments.txt";
    private BufferedReader sectionReader;
    private BufferedWriter sectionWriter;
    private static final String SECTION_FILE_NAME = "project\\project\\resources\\sections.txt";
    private static final String ACCOUNT_FILE_NAME = "project\\project\\resources\\Accounts.txt";
    private static final String SECTIONS_OF_COURSE_FILE_NAME = "project\\project\\resources\\sectionOfCourses.txt";
    private static final String STUDENT_COURSES_FILE_NAME = "project\\project\\resources\\student_courses.txt";






    public ReadWriteFile() {
        try {
            this.studentReader = new BufferedReader(new FileReader(STUDENT_FILE_NAME));
            this.facultyReader = new BufferedReader(new FileReader(FACULTY_FILE_NAME));
            this.courseReader = new BufferedReader(new FileReader(COURSE_FILE_NAME));
            this.majorReader = new BufferedReader(new FileReader(MAJOR_FILE_NAME));

            this.studentWriter = new BufferedWriter(new FileWriter(STUDENT_FILE_NAME, true));
            this.facultyWriter = new BufferedWriter(new FileWriter(FACULTY_FILE_NAME, true));
            this.courseWriter = new BufferedWriter(new FileWriter(COURSE_FILE_NAME, true));
            this.majorWriter = new BufferedWriter(new FileWriter(MAJOR_FILE_NAME, true));
            this.sectionReader = new BufferedReader(new FileReader(SECTION_FILE_NAME));
            this.sectionWriter = new BufferedWriter(new FileWriter(SECTION_FILE_NAME, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    // Read from a file using bufferReader
    public List<String> readFromFile(String fileName) throws IOException {
        List<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        }
        return data;
    }

    //read from all file using the method above
    public List<String> readCourseFile() throws IOException {
        return readFromFile(COURSE_FILE_NAME);
    }

    public List<String> readMajorFile() throws IOException {
        return readFromFile(MAJOR_FILE_NAME);
    }
    

    public List<String> readStudentFile() throws IOException {
        return readFromFile(STUDENT_FILE_NAME);
    }

    public List<String> readStaffFile() throws IOException {
        return readFromFile(STAFF_FILE_NAME);
    }

    public List<String> readAcademicRecordFile() throws IOException {
        return readFromFile(ACADEMIC_RECORD_FILE_NAME);
    }

    public List<String> readFaculty() throws IOException {
        return readFromFile(FACULTY_FILE_NAME);
    }

    public List<String> readSectionFile() throws IOException {
        return readFromFile(SECTION_FILE_NAME);
    }

    //read the accounts file
    public List<String[]> readAccountsFile() throws IOException {
        List<String> lines = readFromFile(ACCOUNT_FILE_NAME);
        List<String[]> accounts = new ArrayList<>();
        for (String line : lines) {
            // Skip the header line
            if (!line.startsWith("Role")) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    accounts.add(new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim()});
                }
            }
        }
        return accounts;
    }

    //write to the account file
    public void addToAccountsFile(String role, String username, String password) throws IOException {
        String accountData = role + "," + username + "," + password;
        writeToFile(ACCOUNT_FILE_NAME, accountData);
    }
    
    
    

    public List<Faculty> readFacultyFile() throws IOException {
    List<String> lines = readFromFile(FACULTY_FILE_NAME);
    List<Faculty> faculties = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    for (String line : lines) {
        String[] parts = line.split(" ");
        if (parts.length >= 6) {
            String name = parts[0];
            String id = parts[1];
            String contactDetails = parts[2];
            String password = parts[3];  
            String major = parts[4];
            LocalDate dateOfBirth = LocalDate.parse(parts[5], formatter); 

            faculties.add(new Faculty(name, id, contactDetails, password, major, dateOfBirth));
        }
    }
    return faculties;
}

//a helper method to parse meeting sessions from a string to a meetingSession object
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

            newMeetingSession meetingSession = new newMeetingSession(day, startTime, endTime, room);
            meetingSessions.add(meetingSession);
        }
    }
    return meetingSessions;
}

//parse student from a string to a newStudent object
public newStudent parseStudentLine(String line) {
    ReadWriteFile rwf = new ReadWriteFile();
    String[] parts = line.split(",");
    if (parts.length >= 6) {
        String name = parts[0];
        String id = parts[1];
        String contactDetails = parts[2];
        String password = parts[3];
        String majorName = parts[4];
        
        LocalDate dateOfBirth = null;
        try {
            dateOfBirth = LocalDate.parse(parts[5], DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        } catch (DateTimeParseException e) {
            dateOfBirth = LocalDate.parse(parts[5], DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        }

        try {
            Major major = rwf.getMajorByName(majorName); // Retrieve Major object
            if (major != null) {
                return new newStudent(name, id, contactDetails, password, major,dateOfBirth);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return null; 
}


//parse section from a string to a newSection object
newSection parseSectionLine(String line) {
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

    //methods to write to all files
    public void writeToFile(String fileName, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(content);
            writer.newLine();
        }
    }
    public void addToMajorFile(String major) throws IOException {
        writeToFile(MAJOR_FILE_NAME, major);
    }
    
    public void addToStudentFile(newStudent student) throws IOException {
        String studentData = formatStudentData(student);
        writeToFile(STUDENT_FILE_NAME, studentData);
    }

    public void addToCourseFile(newCourse course) throws IOException {
        String courseData = formatCourseData(course);
        writeToFile(COURSE_FILE_NAME, courseData);
    }

   

    public void addToMajorFile(Major major) throws IOException {
        String majorData = formatMajorData(major);
        writeToFile(MAJOR_FILE_NAME, majorData);
    }

    public void addToAcademicRecordFile(String studentId, String semester, String courseCode, String grade, int creditHours) throws IOException {
        String record = formatAcademicRecord(studentId, semester, courseCode, grade, creditHours);
        writeToFile(ACADEMIC_RECORD_FILE_NAME, record);
    }
    
    //a helper method to format the acadimic record
    private String formatAcademicRecord(String studentId, String semester, String courseCode, String grade, int creditHours) {
        return studentId + " " + semester + " " + courseCode + " " + grade + " " + creditHours;
    }
    
   
    public void writeStudentAcademicRecordToFile(newStudent student) throws IOException {
        Map<Semester, Map<newCourse, Grade>> academicHistory = student.getAcademicHistory();
        for (Map.Entry<Semester, Map<newCourse, Grade>> semesterEntry : academicHistory.entrySet()) {
            Semester semester = semesterEntry.getKey();
            for (Map.Entry<newCourse, Grade> courseEntry : semesterEntry.getValue().entrySet()) {
                newCourse course = courseEntry.getKey();
                Grade grade = courseEntry.getValue();
                String record = formatAcademicRecord(student.getId(), semester, course, grade);
                writeToFile(ACADEMIC_RECORD_FILE_NAME, record);
            }
        }
    }
    
    private String formatAcademicRecord(String studentId, Semester semester, newCourse course, Grade grade) {
        return studentId + " " + semester.getName() + " " + course.getCourseCode() + " " + grade.getGradeValue() + " " + course.getCredits();
    }
    
    

    private String formatStudentData(newStudent student) {
        return student.getName() + "," +
               student.getId() + "," +
               student.getContactDetails() + "," +
               student.getPassword() + "," +
               student.getMajorName() + "," +
               student.getDateOfBirth();
    }

    //read drom a user profile file weither student or faculty
    private String readFile(BufferedReader reader) throws IOException {
        String line;
        StringBuilder read = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(" ");
            String name = data[0];
            String id = data[1];
            String contactDetails = data[2];
            String password = data[3];
            String major = data[4];
            String dateOfBirth = data[5];
            read.append("Name: ").append(name)
                    .append(", ID: ").append(id)
                    .append(", Contact Details: ").append(contactDetails)
                    .append(", Password: ").append(password)
                    .append(", Major: ").append(major)
                    .append(", Date of Birth: ").append(dateOfBirth)
                    .append("\n");
        }
        return read.toString();
    }
    

   
    private List<String> readFileLines(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
    

    private String formatCourseData(newCourse course) {
        String prerequisites = String.join(";", course.getPrerequisites());
        return course.getCourseCode() + "," + course.getCourseName() + "," + course.getCredits() + "," + course.getMaxCapacity() + "," + prerequisites;
    }
    
    //parse course from a string to a newCourse object
    public newCourse parseCourseLine(String line) {
    String[] parts = line.split(",");
    if (parts.length >= 4) {
        String courseCode = parts[0].trim();
        String courseName = parts[1].trim();
        int credits = Integer.parseInt(parts[2].trim());
        int maxCapacity = Integer.parseInt(parts[3].trim());

        List<String> prerequisites = new ArrayList<>();
        if (parts.length == 5 && !parts[4].isEmpty()) {
            prerequisites = Arrays.asList(parts[4].split(";")).stream().map(String::trim).collect(Collectors.toList());
        }

        return new newCourse(courseCode, courseName, credits, maxCapacity, prerequisites);
    }
    return null; 
}

    private String formatMajorData(Major major) {
        return major.getMajor();
    }

    

            public void AddToStudenFile(newStudent student) throws IOException {
                    studentWriter.write(student.getName() + " " +
                             student.getId() + " " +
                             student.getContactDetails() + " " +
                             student.getPassword() + " " +
                             student.getMajor() + " " +
                             student.getDateOfBirth());
                    studentWriter.newLine();
            }
            
            public void AddToFacultyFile(Faculty faculty) throws IOException {
                facultyWriter.write(faculty.getName() + " " +
                        faculty.getId() + " " +
                        faculty.getContactDetails() + " " +
                        faculty.getPassword() + " " +
                        faculty.getMajor() + " " +
                        faculty.getDateOfBirth());
                facultyWriter.newLine();
            }
            
            public void updateGradeInAcademicRecord(String studentID, String semester, String courseCode, String newGrade) throws IOException {
                List<String> records = readAcademicRecordFile();
                List<String> updatedRecords = new ArrayList<>();
                boolean updated = false;
        
                for (String record : records) {
                    String[] parts = record.split(" ");
                    if (parts[0].equals(studentID) && parts[1].equals(semester) && parts[2].equals(courseCode)) {
                        // Update the grade for this record
                        String updatedRecord = parts[0] + " " + parts[1] + " " + parts[2] + " " + newGrade + " " + parts[4];
                        updatedRecords.add(updatedRecord);
                        updated = true;
                    } else {
                        updatedRecords.add(record);
                    }
                }
        
                if (updated) {
                    overwriteAcademicRecordFile(updatedRecords);
                }
            }
        
            private void overwriteAcademicRecordFile(List<String> records) throws IOException {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACADEMIC_RECORD_FILE_NAME, false))) {
                    for (String record : records) {
                        writer.write(record);
                        writer.newLine();
                    }
                }
            }

            private String formatSectionData(newSection section) {
                String meetingSessionsFormatted = formatMeetingSessions(section.getMeetingSessions());
                return section.getSectionCode() + "," +
                       section.getFaculty() + "," +
                       section.getCourse() + ",\"" +
                       meetingSessionsFormatted + "\"";
            }
            
            private String formatMeetingSessions(List<newMeetingSession> meetingSessions) {
                StringBuilder formattedSessions = new StringBuilder();
                for (newMeetingSession session : meetingSessions) {
                    if (formattedSessions.length() > 0) {
                        formattedSessions.append("; ");
                    }
                    formattedSessions.append(formatSingleSession(session));
                }
                return formattedSessions.toString();
            }
            
            private String formatSingleSession(newMeetingSession session) {
                return session.getDay() + " " +
                       session.getStartTime().toString() + "-" +
                       session.getEndTime().toString() + " " +
                       "Room " + session.getRoom();
            }

            public void addOrUpdateSection(newSection section) throws IOException {
                String formattedSection = formatSectionData(section);
                
                
                writeToFile(SECTION_FILE_NAME, formattedSection);
            }

            public Map<String, Map<String, List<String>>> readEnrollments() throws IOException {
                Map<String, Map<String, List<String>>> enrollments = new HashMap<>();
                List<String> lines = readFromFile(ENROLLMENT_FILE_NAME);
                for (String line : lines) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String[] sectionAndCourse = parts[0].split(",");
                        if (sectionAndCourse.length == 2) {
                            String sectionCode = sectionAndCourse[0];
                            String courseCode = sectionAndCourse[1];
                            List<String> studentIds = Arrays.asList(parts[1].split(","));
                            enrollments.computeIfAbsent(courseCode, k -> new HashMap<>()).put(sectionCode, studentIds);
                        }
                    }
                }
                return enrollments;
            }
            
        
            public void writeOrUpdateEnrollment(String sectionCode, String courseCode, List<String> studentIds) throws IOException {
                Map<String, Map<String, List<String>>> enrollments = readEnrollments();
                enrollments.computeIfAbsent(courseCode, k -> new HashMap<>()).put(sectionCode, studentIds);
            
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENROLLMENT_FILE_NAME, false))) {
                    for (Map.Entry<String, Map<String, List<String>>> courseEntry : enrollments.entrySet()) {
                        for (Map.Entry<String, List<String>> sectionEntry : courseEntry.getValue().entrySet()) {
                            String line = sectionEntry.getKey() + "," + courseEntry.getKey() + ":" + String.join(",", sectionEntry.getValue());
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }
            }
            
            public Major getMajorByName(String majorName) throws IOException {
                Map<String, List<String>> majors = readMajorsWithCourses();
                if (majors.containsKey(majorName)) {
                    List<String> courses = majors.get(majorName);
                    return new Major(majorName, courses); 
                }
                return null; 
            }
            
            public void addOrUpdateMajorWithCourses(String majorName, List<String> courses) throws IOException {
                Map<String, List<String>> majors = readMajorsWithCourses();
                majors.put(majorName, courses);
            
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(MAJOR_FILE_NAME, false))) {
                    for (Map.Entry<String, List<String>> entry : majors.entrySet()) {
                        String line = entry.getKey() + ": " + String.join(", ", entry.getValue());
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            
            public Map<String, List<String>> readMajorsWithCourses() throws IOException {
                Map<String, List<String>> majorsWithCourses = new HashMap<>();
                List<String> lines = readFromFile(MAJOR_FILE_NAME);
            
                for (String line : lines) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String majorName = parts[0].trim();
                        List<String> courses = Arrays.asList(parts[1].trim().split(",\\s*"));
                        majorsWithCourses.put(majorName, courses);
                    }
                }
            
                return majorsWithCourses;
            }
            
            public void updateEnrollments(String sectionCode, String courseCode, String studentId) throws IOException {
                Map<String, Map<String, List<String>>> enrollments = readEnrollments();
            
                enrollments.computeIfAbsent(courseCode, k -> new HashMap<>())
                           .computeIfAbsent(sectionCode, k -> new ArrayList<>())
                           .add(studentId);
            
                writeEnrollments(enrollments);
            }
           
            private void writeEnrollments(Map<String, Map<String, List<String>>> enrollments) throws IOException {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("enrollments.txt", false))) {
                    for (Map.Entry<String, Map<String, List<String>>> courseEntry : enrollments.entrySet()) {
                        for (Map.Entry<String, List<String>> sectionEntry : courseEntry.getValue().entrySet()) {
                            String line = sectionEntry.getKey() + "," + courseEntry.getKey() + ":" +
                                          String.join(",", sectionEntry.getValue());
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }
            }
            

            public void addToFacultyFile(Faculty faculty) throws IOException {
    String facultyData = formatFacultyData(faculty);
    writeToFile(FACULTY_FILE_NAME, facultyData);
}

private String formatFacultyData(Faculty faculty) {
    return faculty.getName() + " " +
           faculty.getId() + " " +
           faculty.getContactDetails() + " " +
           faculty.getPassword() + " " +  
           faculty.getMajor() + " " +
           faculty.getDateOfBirth().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
}

   public Map<String, List<String>> readCourseSections() throws IOException {
    Map<String, List<String>> courseSections = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(SECTIONS_OF_COURSE_FILE_NAME))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length == 2) {
                String course = parts[0];
                String[] sections = parts[1].split(",");
                courseSections.put(course, new ArrayList<>(Arrays.asList(sections)));
            }
        }
    }
    return courseSections;
}

public void writeCourseSection(String course, String section) throws IOException {
    Map<String, List<String>> courseSections = readCourseSections();
    List<String> sections = courseSections.getOrDefault(course, new ArrayList<>());
    if (!sections.contains(section)) {
        sections.add(section);
        courseSections.put(course, sections);
    }
    writeCourseSectionsToFile(courseSections);
}

private void writeCourseSectionsToFile(Map<String, List<String>> courseSections) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(SECTIONS_OF_COURSE_FILE_NAME, false))) {
        for (Map.Entry<String, List<String>> entry : courseSections.entrySet()) {
            String line = entry.getKey() + " " + String.join(",", entry.getValue());
            writer.write(line);
            writer.newLine();
        }
    }
}
public Set<String> getEnrolledCourses(String studentId) throws IOException {
    Set<String> enrolledCourses = new HashSet<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_COURSES_FILE_NAME))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length == 2 && parts[0].equals(studentId)) {
                enrolledCourses.add(parts[1]);
            }
        }
    }
    return enrolledCourses;
}
List<newMeetingSession> getCourseSchedule(String courseCode) throws IOException {
    List<newMeetingSession> courseSchedule = new ArrayList<>();
    Map<String, String> sectionDetails = readSectionsDetails(); 

    for (String section : sectionDetails.keySet()) {
        if (section.startsWith(courseCode)) {
            String details = sectionDetails.get(section);
            String[] sessionDetails = details.split("\"")[1].split(";");

            for (String session : sessionDetails) {
                newMeetingSession meeting = parseSession(session.trim());
                if (meeting != null) {
                    courseSchedule.add(meeting);
                }
            }
        }
    }
    return courseSchedule;
}

Map<String, String> readSectionsDetails() throws IOException {
    Map<String, String> sectionDetails = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(SECTION_FILE_NAME))) {
        String line;
        while ((line = reader.readLine()) != null) {
            int thirdCommaIndex = findNthComma(line, 3);
            if (thirdCommaIndex != -1) {
                String sectionCode = line.substring(0, thirdCommaIndex).trim();
                String details = line.substring(thirdCommaIndex + 1).trim();
                sectionDetails.put(sectionCode, details);
            }
        }
    }
    return sectionDetails;
}


private int findNthComma(String str, int n) {
    int count = 0;
    for (int i = 0; i < str.length(); i++) {
        if (str.charAt(i) == ',') {
            count++;
            if (count == n) {
                return i;
            }
        }
    }
    return -1; 
}

private newMeetingSession parseSession(String session) {
    try {
        String[] parts = session.split(" ");
        String day = parts[0];
        String[] times = parts[1].split("-");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse(times[0], timeFormatter);
        LocalTime endTime = LocalTime.parse(times[1], timeFormatter);
        String room = parts[2];

        return new newMeetingSession(day, startTime, endTime, room);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

private boolean timesOverlap(newMeetingSession session1, newMeetingSession session2) {
    return session1.hasOverlap(session2);
}
    public void closeReaders() {
        try {
            if (studentReader != null) {
                studentReader.close();
            }
            if (staffReader != null) {
                staffReader.close();
            }
            if (facultyReader != null) {
                facultyReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
