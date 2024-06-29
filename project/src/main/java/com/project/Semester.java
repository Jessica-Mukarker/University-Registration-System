package com.project;

import java.time.*;
import java.util.*;

public class Semester {
    private String semesterName;
    private int year;
    private LocalDate startDate;
    private LocalDate endDate;

    public Semester(String name, int year, LocalDate startDate, LocalDate endDate) {
        this.semesterName = name;
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return semesterName + " " + year;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isInProgress(LocalDate currentDate) {
        return currentDate.isAfter(startDate) && !currentDate.isAfter(endDate);
    }

    public boolean isUpcoming(LocalDate currentDate) {
        return currentDate.isBefore(startDate);
    }

    public boolean isFinished(LocalDate currentDate) {
        return currentDate.isAfter(endDate);
    }
}

