package com.project;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Registrar {
    private ReadWriteFile rwf;
    private Map<Semester, List<newCourse>> mapCoursesOfEachSemester;
    private Map<Major, List<newCourse>> mapCoursesOfEachMajor;
    private Map<newCourse, List<newSection>> mapSectionsOfEachCourse;
    private Map<newCourse, List<newStudent>> mapStudentinEachCourse;
    private Map<newSection, List<newStudent>> mapStudentsinEachSection;
    private Map<newStudent, List<newSection>> mapOfWhatCoursesTheStudentHas;
    private List<Major> majors;
    private List<newStudent> students;
    private List<Faculty> faculties;
    private List<Semester> semesters;
    private List<newCourse> courses;
    private ExecutorService executorService;
    private Map<String, Faculty> facultyRegistry;
    private Map<newCourse, List<newSection>> courseSections;

    public Registrar() {
        this.rwf = new ReadWriteFile();
        this.mapCoursesOfEachSemester = new HashMap<Semester, List<newCourse>>();
        this.mapCoursesOfEachMajor = new HashMap<Major, List<newCourse>>();
        this.mapSectionsOfEachCourse = new HashMap<newCourse, List<newSection>>();
        this.mapStudentinEachCourse = new HashMap<newCourse, List<newStudent>>();
        this.mapOfWhatCoursesTheStudentHas = new HashMap<newStudent, List<newSection>>();
        this.mapStudentsinEachSection = new HashMap<>();
        this.students = new ArrayList<>();
        this.faculties = new ArrayList<>();
        this.semesters = new ArrayList<>();
        this.majors = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(5);
        this.facultyRegistry = new HashMap<>();
        this.courseSections = new HashMap<>();
        this.courses = new ArrayList<>();

    }

    public Registrar(ReadWriteFile rwf) {
        this.rwf = rwf;
        this.mapCoursesOfEachSemester = new HashMap<Semester, List<newCourse>>();
        this.mapCoursesOfEachMajor = new HashMap<Major, List<newCourse>>();
        this.mapSectionsOfEachCourse = new HashMap<newCourse, List<newSection>>();
        this.mapStudentinEachCourse = new HashMap<newCourse, List<newStudent>>();
        this.mapOfWhatCoursesTheStudentHas = new HashMap<newStudent, List<newSection>>();
        this.mapStudentsinEachSection = new HashMap<>();
        this.students = new ArrayList<>();
        this.faculties = new ArrayList<>();
        this.semesters = new ArrayList<>();
        this.majors = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(5);
        this.facultyRegistry = new HashMap<>();
        this.courseSections = new HashMap<>();
        this.courses = new ArrayList<>();

    }

    public Optional<newStudent> createStudent(String name, String id, String contactDetails, String password,
            LocalDate dateOfBirth, Major major) throws IOException {
        List<String> list = rwf.readMajorFile();
        newStudent newStudent;
        if (list.contains(major)) {
            newStudent = new newStudent(name, id, contactDetails, password, dateOfBirth, major);
            students.add(newStudent);
            rwf.AddToStudenFile(newStudent);
            return Optional.of(newStudent);
        } else {
            System.out.println("There's no such major to add the student " + name + " in!");
            return Optional.empty();
        }
    }
   
    //read the grades from file
    public void enterGradesFromFile(String fileName) throws IOException {
        List<Future<String>> futures = new ArrayList<>();

        try (Scanner scanner = new Scanner(Registrar.class.getResourceAsStream(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(" ");

                if (data.length == 3) {
                    String studentID = data[0];
                    String courseID = data[1];
                    String grade = data[2];

                    futures.add(processGrade(studentID, courseID, grade));
                } else {
                    System.out.println("Invalid input format: " + line);
                }
            }
        }

        for (Future<String> future : futures) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private Optional<newStudent> findStudentById(String studentID) {
        return students.stream()
                .filter(student -> student.getId().equals(studentID))
                .findFirst();
    }

    private Optional<newCourse> findCourseById(String courseID) {
        return mapCoursesOfEachSemester.values().stream()
                .flatMap(List::stream)
                .filter(course -> course.getCourseCode().equals(courseID))
                .findFirst();
    }

    //process the grade for a dtudent in a specific course
    private Future<String> processGrade(String studentID, String courseID, String grade) {
        return executorService.submit(() -> {
            Optional<newStudent> optionalStudent = findStudentById(studentID);
            Optional<newCourse> optionalCourse = findCourseById(courseID);

            if (optionalStudent.isPresent() && optionalCourse.isPresent()) {
                newStudent student = optionalStudent.get();
                newCourse course = optionalCourse.get();

                student.setGrade(course.getCourseName(), grade);

                return "Processed Grade - StudentID: " + studentID + ", CourseID: " + courseID + ", Grade: " + grade +
                        ", Academic Standing: " + calculateAcademicStanding(student);
            } else {
                return "Invalid student ID or course ID: " + studentID + ", " + courseID;
            }
        });
    }

    public double calculateGPA(newStudent student) {
        List<Future<Double>> futures = new ArrayList<>();

        Map<newCourse, Grade> grades = student.getGrades();

        for (Map.Entry<newCourse, Grade> entry : grades.entrySet()) {
            newCourse course = entry.getKey();
            double grade = entry.getValue().getGPAValue();

            futures.add(calculateCourseGPA(course, grade));
        }

        double totalGPA = futures.parallelStream()
                .mapToDouble(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return 0.0;
                    }
                })
                .sum();

        return totalGPA / grades.size();
    }

    private Future<Double> calculateCourseGPA(newCourse course, double grade) {
        return executorService.submit(() -> {
            return (double) grade / 100.0 * 4.0;
        });
    }

    public String calculateAcademicStanding(newStudent student) {
        double gpa = calculateGPA(student); 
        if (gpa >= 3.5) {
            return "DeansList";
        } else if (gpa >= 3.0) {
            return "Honours";
        } else {
            return "Good Standing";
        }
    }

    public void generateAcademicStandingReport(newStudent student) {
        System.out.println(
                "Academic Standing Report for " + student.getName() + ": " + calculateAcademicStanding(student));
    }


    public Optional<Faculty> createFaculty(String name, String id, String contactDetails, String password, String major,
            String dateOfBirthStr) throws IOException {
        List<String> list = rwf.readMajorFile();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr, formatter); 

        Faculty newFaculty;
        if (list.contains(major)) {
            newFaculty = new Faculty(name, id, contactDetails, password, major, dateOfBirth);
            faculties.add(newFaculty);
            rwf.AddToFacultyFile(newFaculty);
            return Optional.of(newFaculty);
        } else {
            System.out.println("There's no such major to add this Faculty member " + name + " in!");
            return Optional.empty();
        }
    }

    public Semester createSemester(String name, int year, LocalDate startDate, LocalDate endDate) {
        Semester semester = new Semester(name, year, startDate, endDate);
        semesters.add(semester);
        return semester;
    }

    private boolean hasTimeConflict(Faculty faculty, newMeetingSession newMeetingSession) {
        return courses.stream()
                .filter(course -> course.getFaculties().equals(faculty))
                .anyMatch(course -> {
                    try {
                        return course.hasTimeConflict(newMeetingSession);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                });
    }

    public void browseAvailableCourses() {
        System.out.println("Available Courses:");
        for (newCourse course : courses) {
            System.out.println(course.getCourseName());
        }
    }

    public void viewPrerequisites(String courseName) throws IOException {
        newCourse course = findCourseByName(courseName);
        if (course != null) {
            System.out.println("Prerequisites for " + courseName + ": " + course.getPrerequisites());
        } else {
            System.out.println("Course not found: " + courseName);
        }
    }

    public Optional<newSection> createSection(String sectionCode, String courseName, String facultyName) {
        try {
            newCourse course = findCourseByName(courseName);
            Faculty faculty = findFacultyByName(facultyName);
            if (course != null && faculty != null) {
                List<newMeetingSession> meetingSessions = new ArrayList<>(); 
                newSection section = new newSection(sectionCode, courseName, facultyName, meetingSessions);
                return Optional.of(section);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private newCourse findCourseByName(String courseName) throws IOException {
        return rwf.readCourseFile().stream()
                .map(record -> record.split(","))
                .filter(parts -> parts[1].trim().equalsIgnoreCase(courseName))
                .findFirst()
                .map(parts -> {
                    String courseCode = parts[0].trim();
                    String name = parts[1].trim();
                    int credits = Integer.parseInt(parts[2].trim());
                    int maxCapacity = Integer.parseInt(parts[3].trim());
                    List<String> prerequisites = (parts.length > 4) ? Arrays.asList(parts[4].trim().split(";"))
                            : List.of();
                    return new newCourse(courseCode, name, credits, maxCapacity, prerequisites);
                })
                .orElse(null);
    }

    private Faculty findFacultyByName(String facultyName) throws IOException {
        return rwf.readFacultyFile().stream()
                .filter(faculty -> faculty.getName().equalsIgnoreCase(facultyName))
                .findFirst()
                .orElse(null);
    }

    private boolean sectionHasMeetingSessionConflicts(newCourse course, List<newMeetingSession> newMeetingSessions) {
        List<newMeetingSession> existingMeetingSessions = mapSectionsOfEachCourse.get(course)
                .stream()
                .flatMap(section -> section.getMeetingSessions().stream())
                .collect(Collectors.toList());

        return newMeetingSessions.stream().anyMatch(newMeetingSession -> existingMeetingSessions.stream()
                .anyMatch(existingMeetingSession -> existingMeetingSession.hasOverlap(newMeetingSession)));
    }

    public Major createMajor(String majorName) {
        Major major = new Major(majorName);
        majors.add(major);
        mapCoursesOfEachMajor.put(major, new ArrayList<>());
        return major;
    }

    private boolean studentHasMeetingSessionConflicts(newStudent student, List<newMeetingSession> newMeetingSessions)
            throws IOException {
        List<newMeetingSession> existingMeetingSessions = student.getEnrolledCourses().stream()
                .flatMap(course -> mapSectionsOfEachCourse.get(course).stream())
                .flatMap(section -> section.getMeetingSessions().stream())
                .collect(Collectors.toList());

        return newMeetingSessions.stream().anyMatch(newMeetingSession -> existingMeetingSessions.stream()
                .anyMatch(existingMeetingSession -> existingMeetingSession.hasOverlap(newMeetingSession)));
    }

    private Optional<newSection> findAvailableSectionForStudent(newStudent student, newCourse course)
            throws IOException {
        List<newMeetingSession> existingMeetingSessions = student.getEnrolledCourses().stream()
                .flatMap(c -> mapSectionsOfEachCourse.get(c).stream())
                .flatMap(section -> section.getMeetingSessions().stream())
                .collect(Collectors.toList());

        return mapSectionsOfEachCourse.get(course).stream()
                .filter(section -> section.getMeetingSessions().stream()
                        .noneMatch(session -> existingMeetingSessions.stream()
                                .anyMatch(existingSession -> session.hasOverlap(existingSession))))
                .findFirst();
    }

    private Optional<newStudent> findStudent(newStudent student) {
        return students.stream()
                .filter(s -> s.getId().equals(student.getId()))
                .findFirst();
    }

    private Optional<newCourse> findCourse(newCourse course) {
        return mapCoursesOfEachSemester.values().stream()
                .flatMap(List::stream)
                .filter(c -> c.getCourseCode().equals(course.getCourseCode()))
                .findFirst();
    }

    public List<newSection> allCoursesStudentTakesInThisSemester(newStudent student, Semester semester) {
        if (students.contains(student)) {
            List<newSection> sectionsInSemester = mapOfWhatCoursesTheStudentHas
                    .getOrDefault(student, Collections.emptyList())
                    .stream()
                    .filter(section -> mapSectionsOfEachCourse.entrySet().stream()
                            .filter(entry -> entry.getValue().contains(section))
                            .anyMatch(entry -> mapCoursesOfEachSemester.get(semester).contains(entry.getKey())))
                    .collect(Collectors.toList());

            if (!sectionsInSemester.isEmpty()) {
                return sectionsInSemester;
            } else {
                System.out.println("Student's Schedule for the Semester " + semester.getName() + " is Empty");
            }
        } else {
            System.out.println("There's no such student: " + student.getName());
        }
        return Collections.emptyList();
    }

    public void assignSectionToFaculty(Faculty faculty, newCourse course, newSection section) {
        Optional<Faculty> optionalFaculty = Optional.ofNullable(facultyRegistry.get(faculty.getId()));
        if (optionalFaculty.isPresent()) {
            if (courseSections.containsKey(course)) {
                List<newSection> sections = courseSections.get(course);
                if (sections.contains(section)) {
                    section.setFaculty(faculty.getName());
                    System.out.println("Faculty " + faculty.getName() + " assigned to teach section " +
                            section.getSectionCode() + " of course " + section.getCourse());
                } else {
                    System.out.println("The section does not exist for the course: " + course.getCourseName());
                }
            } else {
                System.out.println("No sections available for the course: " + course.getCourseName());
            }
        } else {
            System.out.println("No such faculty exists: " + faculty.getName());
        }
    }

    public List<newSection> createStudentSchedule(newStudent student) {
        if (students.contains(student)) {
            List<newSection> studentSections = mapOfWhatCoursesTheStudentHas.getOrDefault(student,
                    Collections.emptyList());

            if (!studentSections.isEmpty()) {
                return studentSections;
            } else {
                System.out.println("Student's schedule is empty. Please register for courses.");
            }
        } else {
            System.out.println("There's no such student: " + student.getName());
        }
        return Collections.emptyList();
    }

}
