package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * A grade (nota) of a student in a subject and period (table registros_academicos).
 */
public class AcademicRecord {

    public static final double MIN_GRADE = 0.0;
    public static final double MAX_GRADE = 5.0;
    public static final double PASSING_GRADE = 3.0;

    private int id;
    private int studentId;
    private String studentName;
    private int subjectId;
    private String subjectName;
    private int gradeId;
    private String gradeName;
    private int teacherId;
    private int academicYear;
    private AcademicPeriod period;
    private double value;
    private String notes;
    private LocalDate recordDate;

    public AcademicRecord() {
    }

    public AcademicRecord(int studentId, int subjectId, int teacherId, int academicYear, AcademicPeriod period,
                          double value) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.teacherId = teacherId;
        this.academicYear = academicYear;
        this.period = period;
        setValue(value);
        this.recordDate = LocalDate.now();
    }

    public boolean isFailing() {
        return value < PASSING_GRADE;
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

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public AcademicPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AcademicPeriod period) {
        this.period = period;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if (value < MIN_GRADE || value > MAX_GRADE) {
            throw new IllegalArgumentException("La nota debe estar entre 0.0 y 5.0.");
        }
        this.value = Math.round(value * 10) / 10.0;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }
}
