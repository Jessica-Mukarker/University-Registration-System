package com.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class newStudent extends UserProfile {
    private ReadWriteFile rwf;
    private String name;
    private String id;
    private String contactDetails;
    private LocalDate dateOfBirth;
    private Major major;
    private int studentYear;
    private Set<newCourse> coursesFinished;
    private Set<newCourse> coursesToTake;
    private Schedule schedule;
    private Set<newCourse> enrolledCourses;
    private String currentSemester;
    private Map<newCourse, Grade> grades; 
    private Faculty advisor;
    private Map<Semester, Map<newCourse, Grade>> academicHistory;
    private static final String STUDENT_COURSES_FILE_NAME = "project\\project\\resources\\student_courses.txt";

    public newStudent(String name, String id, String contactDetails, LocalDate dateOfBirth, String password,
            Major major, int studentYear, Set<newCourse> coursesFinished,
            Set<newCourse> coursesToTake, Set<newCourse> enrolledCourses,
            String currentSemester, Faculty advisor) {

        super(name, id, contactDetails, Role.STUDENT, password);
        this.dateOfBirth = dateOfBirth;
        this.major = major;
        this.studentYear = studentYear;
        this.coursesFinished = coursesFinished;
        this.coursesToTake = coursesToTake;
        this.enrolledCourses = enrolledCourses;
        this.currentSemester = currentSemester;
        this.schedule = new Schedule();
        this.advisor = advisor;
        this.grades = new HashMap<>();
        this.academicHistory = new HashMap<>();
    }


    public newStudent(String name, String id, String contactDetails, String password,
            LocalDate dateOfBirth,Major major) {
 super(name, id, contactDetails, Role.STUDENT, password);
        this.dateOfBirth = dateOfBirth;
        this.id=id;
        this.contactDetails=contactDetails;
        this.major = major;
    }

    public newStudent(String name2, String id2, String contactDetails2, String password, Major major2,
            LocalDate dateOfBirth2) {
                 super(name2, id2, contactDetails2, Role.STUDENT, password);

                this.name=name2;
                this.id=id2;
                this.contactDetails=contactDetails2;
                this.major=major2;
                this.dateOfBirth=dateOfBirth2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getMajorName(){
        return major.getMajor();
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public int getStudentYear() {
        return studentYear;
    }

    public void setStudentYear(int studentYear) {
        this.studentYear = studentYear;
    }

    public Set<newCourse> getCoursesFinished() {
        return coursesFinished;
    }

    public void setCoursesFinished(Set<newCourse> coursesFinished) {
        this.coursesFinished = coursesFinished;
    }

    public Set<newCourse> getCoursesToTake() {
        return coursesToTake;
    }

    public void setCoursesToTake(Set<newCourse> coursesToTake) {
        this.coursesToTake = coursesToTake;
    }

   
    public void setEnrolledCourses(Set<newCourse> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public String getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(String currentSemester) {
        this.currentSemester = currentSemester;
    }

    public Faculty getAdvisor() {
        return advisor;
    }

    public void setAdvisor(Faculty advisor) {
        this.advisor = advisor;
    }

    public Schedule getSchedule() {
        return schedule;
    }
    
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
    

    //add a class to this student's schedule
    public void addClassToSchedule(newCourse course, List<newMeetingSession> meetingSessions) {
        ScheduledCourse scheduledCourse = new ScheduledCourse(course, meetingSessions);
        if (schedule.getCoursesAt(meetingSessions).isEmpty()) {
            schedule.addCourse(scheduledCourse);
        } else {
            notifyScheduleConflict(scheduledCourse);
        }
    }
    
    //remove class from this student schedual (drop this course)
    public void removeClassFromSchedule(newCourse course) {
        schedule.removeCourse(new ScheduledCourse(course, null)); 
    }

    //check if this student has any conflicts in their schedual
    public boolean hasScheduleConflict(newCourse course, List<newMeetingSession> newMeetingSessions) {
        for (newMeetingSession newSession : newMeetingSessions) {
            if (!getClassesAt(newSession).isEmpty()) {
                return true; 
            }
        }
        return false; 
    }
    
    //return the class that is in this time
    public List<ScheduledCourse> getClassesAt(newMeetingSession meetingSession) {
        return schedule.getCoursesAt(meetingSession);
    }
    
    //a printer method to notify if 
    public void notifyScheduleConflict(ScheduledCourse course) {
        System.out.println("Schedule conflict detected for course: " + course.getCourse().getCourseName());
    }

    //student's grade in that course
    public void addGrade(newCourse course, Grade grade) {
        grades.put(course, grade);
        System.out
                .println(name + " received grade " + grade.getGradeValue() + " in course: " + course.getCourseName());
    }


    public void setGrades(Map<newCourse, Grade> grades) {
        this.grades = grades;
    }


    public void setGrade(String courseCode, String grade) throws IOException {
        rwf.updateGradeInAcademicRecord(this.id, currentSemester,courseCode, grade);
    }

   
    
    //check first if that course meets all prerequisits it's supposed to meet
    boolean meetsPrerequisites(newCourse course) {
        List<String> prerequisites = course.getPrerequisites();
        for (String prerequisite : prerequisites) {
            if (!enrolledCourses.contains(prerequisite)) {
                return false; 
            }
        }
        return true;
    }

 
   
    //check if that student is enrolled in this course
    public boolean isEnrolledInCourse(newCourse course) {
        return enrolledCourses.contains(course);
    }

    //enroll students in that course if he took all prerequisites
    public void enrollInCourse(newCourse course) {
        if (meetsPrerequisites(course)) {
            enrolledCourses.add(course);
            System.out.println(name + " enrolled in course: " + course.getCourseName());
        } else {
            System.out.println(name + " cannot enroll in course: " + course.getCourseName() +
                    " due to unmet prerequisites.");
        }
    }

    //student drops course
    public void dropCourse(newCourse course) {
        enrolledCourses.remove(course);
        System.out.println(name + " dropped course: " + course.getCourseName());
    }

    //calculate the total gpa for that student depending in the courses and credits
    public double calculateGPA() {      
        double totalPoints = 0;
        int totalCredits = 0;

        for (Map.Entry<newCourse, Grade> entry : grades.entrySet()) {
            newCourse course = entry.getKey();
            Grade grade = entry.getValue();

            double gradePoints = grade.getGPAValue();
            totalPoints += gradePoints * course.getCredits();
            totalCredits += course.getCredits();
        }

        return totalPoints / totalCredits;
    }

    public Map<newCourse, Grade> getGrades() {
        return grades;
    }

    public void addGrade(Semester semester, newCourse course, Grade grade) {
        academicHistory.computeIfAbsent(semester, k -> new HashMap<>()).put(course, grade);
    }

    public Map<Semester, Map<newCourse, Grade>> getAcademicHistory() {
        return academicHistory;
    }

    //return a set of the courses this student is enrolled in
public Set<String> getEnrolledCourses() throws IOException {
    Set<String> enrolledCourses = new HashSet<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_COURSES_FILE_NAME))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length == 2 && parts[0].equals(this.id)) {
                enrolledCourses.add(parts[1]);
            }
        }
    }
    return enrolledCourses;
}

    //read the student-course file
     public Map<String, List<String>> readStudentCourses() throws IOException {
        Map<String, List<String>> studentCourses = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_COURSES_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                studentCourses.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
            }
        }
        return studentCourses;
    }

    //write to the the student-course file
    public void addStudentCourse(String studentId, String courseCode) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENT_COURSES_FILE_NAME, true))) {
            writer.write(studentId + " " + courseCode);
            writer.newLine();
        }
    }


    @Override
    public String toString() {
        return "newStudent{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", contactDetails='" + contactDetails + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", major='" + major.getMajor() + '\'' +
                ", studentYear=" + studentYear +
                ", coursesFinished=" + coursesFinished +
                ", coursesToTake=" + coursesToTake +
                ", enrolledCourses=" + enrolledCourses +
                ", currentSemester='" + currentSemester + '\'' +
                ", advisor='" + advisor.getName() + '\'' +
                '}';
    }
}
