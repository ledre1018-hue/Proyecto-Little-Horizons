package com.littlehorizons.erp.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Daily attendance of a student in a subject (table asistencias).
 */
public class Attendance {

    private int id;
    private int studentId;
    private String studentName;
    private int subjectId;
    private String subjectName;
    private LocalDate date;
    private AttendanceStatus status;
    private String notes;
    private Integer recordedBy;

    public Attendance() {
    }

    public Attendance(int studentId, int subjectId, LocalDate date, AttendanceStatus status) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        setDate(date);
        setStatus(status);
    }

    public boolean isAbsence() {
        return status == AttendanceStatus.ABSENT;
    }

    /** Monday of the week of this attendance, used to group by week. */
    public LocalDate getWeekStart() {
        return date.with(DayOfWeek.MONDAY);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de asistencia es obligatoria y no puede ser futura.");
        }
        this.date = date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado de asistencia es obligatorio.");
        }
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Integer recordedBy) {
        this.recordedBy = recordedBy;
    }
}
