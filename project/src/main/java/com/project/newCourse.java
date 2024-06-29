package com.project;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class newCourse {
    private ReadWriteFile rwf;
    private String courseCode;
    private String courseName;
    private int maxCapacity;
    private List<String> enrolledStudents;
    private List<String> facultyNames; 
    private int credits;
    private List<String> sectionCodes; 
    private List<String> prerequisiteCourseCodes; 

    public newCourse(String courseCode, String courseName, int maxCapacity, int credits, List<String> prerequisiteCourseCodes) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.maxCapacity = maxCapacity;
        this.credits = credits;
        this.enrolledStudents = new ArrayList<>();
        this.facultyNames = new ArrayList<>();
        this.sectionCodes = new ArrayList<>();
        this.prerequisiteCourseCodes = prerequisiteCourseCodes;
    }

    public newCourse(String courseCode, String courseName, int maxCapacity, int credits, List<String> prerequisiteCourseCodes,ReadWriteFile rwf) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.maxCapacity = maxCapacity;
        this.credits = credits;
        this.enrolledStudents = new ArrayList<>();
        this.facultyNames = new ArrayList<>();
        this.sectionCodes = new ArrayList<>();
        this.prerequisiteCourseCodes = prerequisiteCourseCodes;
        this.rwf=rwf;
    }
    
    public void setReadWriteFile(ReadWriteFile rwf) {
        this.rwf = rwf;
    }

    //read the file and returns all students enrolled in this course
    public List<newStudent> getStudentsEnrolledInCourse() {
    ReadWriteFile rwf = new ReadWriteFile();
    try {
        Map<String, Map<String, List<String>>> enrollments = rwf.readEnrollments();
        List<String> enrolledStudentIds = enrollments.getOrDefault(this.courseCode, new HashMap<>())
                                                      .values().stream()
                                                      .flatMap(List::stream)
                                                      .distinct()
                                                      .collect(Collectors.toList());

        List<String> studentFileLines = rwf.readStudentFile();
        return studentFileLines.stream()
                .map(this::parseStudentLine)
                .filter(student -> enrolledStudentIds.contains(student.getId()))
                .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }
        
        //a helper method to parse each line into a newStudent Object
        private newStudent parseStudentLine(String line) {
            ReadWriteFile rwf = new ReadWriteFile();
            String[] parts = line.split(" ");
            if (parts.length >= 6) {
                String name = parts[0];
                String id = parts[1];
                String contactDetails = parts[2];
                String password = parts[3]; 
                String majorName = parts[4];
                LocalDate dateOfBirth = LocalDate.parse(parts[5], DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                try {
                    Major major = rwf.getMajorByName(majorName); 
                    if (major != null) {
                        return new newStudent(name, id, contactDetails, password, dateOfBirth, major);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null; 
        }


//reads all section of that course from a file and returns them as a list of newSection
public List<newSection> getSectionsForCourse() {
    ReadWriteFile rwf = new ReadWriteFile();
    try {
        List<String> sectionFileLines = rwf.readSectionFile();
        return sectionFileLines.stream()
                .map(this::parseSectionLine)
                .filter(section -> section.getCourse().equals(this.courseCode))
                .collect(Collectors.toList());
    } catch (IOException e) {
        e.printStackTrace();
        return Collections.emptyList();
    }
}

//a helper metho to parse the line into a newSection
private newSection parseSectionLine(String line) {
    String[] parts = line.split(",", 4); // Limit to 4 to avoid splitting the meeting sessions
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

//a helper metho to parse the line into a meeting session
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




    //get all students enrolled in this course by their ids
    public List<String> getEnrolledStudentIds() {
    ReadWriteFile rwf = new ReadWriteFile();
    try {
        Map<String, Map<String, List<String>>> enrollments = rwf.readEnrollments();
        return enrollments.entrySet().stream()
                .filter(entry -> entry.getKey().equals(this.courseCode))
                .flatMap(entry -> entry.getValue().values().stream())
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    } catch (IOException e) {
        e.printStackTrace();
        return Collections.emptyList();
    }
}

//returns the section codes of this course from a file
public List<String> getSectionCodesForCourse() {
    ReadWriteFile rwf = new ReadWriteFile();
    try {
        Map<String, Map<String, List<String>>> enrollments = rwf.readEnrollments();
        return enrollments.entrySet().stream()
                .filter(entry -> entry.getKey().equals(this.courseCode))
                .flatMap(entry -> entry.getValue().keySet().stream())
                .collect(Collectors.toList());
    } catch (IOException e) {
        e.printStackTrace();
        return Collections.emptyList();
    }
}

    //a method to display the section information in a presentable way
  public void displaySectionsInfo() {
        ReadWriteFile rwf = new ReadWriteFile();
        try {
            List<String> sectionLines = rwf.readSectionFile();
            List<String> relevantSections = sectionLines.stream()
                .filter(line -> line.contains(this.courseName))
                .collect(Collectors.toList());

            System.out.println("Sections for Course: " + courseCode + " - " + courseName);
            for (String sectionLine : relevantSections) {
                String[] parts = sectionLine.split(",");
                if (parts.length > 3) {
                    String sectionCode = parts[0];
                    String facultyName = parts[1];
                    String meetingSessions = parts[3]; 
                    System.out.println("Section Code: " + sectionCode);
                    System.out.println("Faculty Name: " + facultyName);
                    System.out.println("Meeting Sessions: " + meetingSessions);
                    System.out.println("------------------------------");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading section file.");
        }
    }
    

   

    public void addSection(String section) {
        sectionCodes.add(section);
    }

    public void removeSection(String section) {
        sectionCodes.remove(section);
    }
  

//check weither the sections in this course have conflicts 
public boolean hasTimeConflict(newMeetingSession newSession) throws IOException {
    ReadWriteFile rwf = new ReadWriteFile();
    List<newSection> sections = getSectionsForCourse();

    return sections.stream()
        .flatMap(section -> section.getMeetingSessions().stream())
        .anyMatch(existingSession -> existingSession.hasOverlap(newSession));
}


//a method to remove student from this course
public void removeStudent(String student) {
    if (enrolledStudents.contains(student)) {
        enrolledStudents.remove(student); 
        System.out.println(student+ " removed from " + courseCode + " - " + courseName);

    } else {
        System.out.println(student + " is not enrolled in " + courseCode + " - " + courseName);
    }
}

    //add a student to this course
    public void addStudent(String studentId, String sectionCode) {
        ReadWriteFile rwf = new ReadWriteFile();
        try {
            if (getAvailableSeats() > 0 && !enrolledStudents.contains(studentId)) {
                enrolledStudents.add(studentId);
                System.out.println(studentId + " added to " + courseCode + " - " + courseName);
                
                rwf.updateEnrollments(sectionCode, courseCode, studentId);
            } else {
                System.out.println("Cannot enroll student. Course full or student already enrolled.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void addCourse(String course) {
        prerequisiteCourseCodes.add(course);
    }

    public void removeCourse(String course) {
        prerequisiteCourseCodes.remove(course);
    }

    public int getCredits() {
        return credits;
    }
    
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public List<String> getFaculties() {
        return facultyNames;
    }

    public void setFaculties(List<String> faculties) {
        this.facultyNames = faculties;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

   

    public List<String> getSections() {
        return sectionCodes;
    }

    public void setSections(List<String> sections) {
        this.sectionCodes = sections;
    }

    //Displays the list of enrolled students in the current course.
    public void displayEnrolledStudents() {
        System.out.println("Enrolled Students in " + courseCode + " - " + courseName);
        for (String student : enrolledStudents) {
            System.out.println(student);
        }
    }

    //Displays the list of courses enrolled by a specific student
    public void displayEnrolledCourses(newStudent student) {
        System.out.println("Courses enrolled by " + student.getName());
        for (newCourse course : student.getCoursesToTake()) {
            System.out.println(course.getCourseCode() + " - " + course.getCourseName());
        }
    }

    //get how many seats left in that course
    public int getAvailableSeats() {
        return maxCapacity - enrolledStudents.size();
    }

    //check if this course is already full
    public boolean isFull() {
        return enrolledStudents.size() >= maxCapacity;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<String> getInstructors() {
        return facultyNames;
    }

    public List<String> getPrerequisites() {
        return prerequisiteCourseCodes;
    }

    public void addPrerequisite(String prerequisite) {
        prerequisiteCourseCodes.add(prerequisite);
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisiteCourseCodes = prerequisites;
    }

    //Enrolls a student in the current course if there are available seats
    public void enrollStudent(String student) {
        if (getAvailableSeats() > 0) {
            enrolledStudents.add(student);
            System.out.println(student + " enrolled in " + courseCode + " - " + courseName);
        } else {
            System.out.println("Course " + courseCode + " - " + courseName + " is full. Cannot enroll student.");
        }
    }

}
