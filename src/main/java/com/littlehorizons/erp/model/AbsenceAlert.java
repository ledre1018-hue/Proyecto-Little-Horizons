package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * A student with too many absences in one week. Result of a calculation, not a table.
 */
public class AbsenceAlert {

    private final int studentId;
    private final String studentName;
    private final LocalDate weekStart;
    private final long absences;

    public AbsenceAlert(int studentId, String studentName, LocalDate weekStart, long absences) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.weekStart = weekStart;
        this.absences = absences;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekStart.plusDays(6);
    }

    public long getAbsences() {
        return absences;
    }
}
