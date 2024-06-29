package com.project;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.project.StudentScheduleManager.ClassDetails;

public class ScheduleMain {
    public static void main(String[] args) {
        try {
            LocalDate dob = LocalDate.parse("01-01-2000", DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            newStudent alice = new newStudent("Alice", "1001", "555-0101", "alice123", dob, new Major("Biology"));

            StudentScheduleManager scheduleManager = new StudentScheduleManager();

            Map<String,List<String>> aliceSchedule = scheduleManager.generateStudentSchedule(alice.getId());

            for (Map.Entry<String, List<String>> scheduleEntry : aliceSchedule.entrySet()) {
                String course = scheduleEntry.getKey();
                List<String> scheduleDetails = scheduleEntry.getValue();
            
                System.out.println("Course: " + course);
                for (String detail : scheduleDetails) {
                    System.out.println(" - " + detail);
                }
                System.out.println();
        
            }
            
           scheduleManager.addCourse(alice.getId(), "MATH201");
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
