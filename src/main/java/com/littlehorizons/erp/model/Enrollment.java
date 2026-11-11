package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Enrollment of a student in a grade for one academic year (table matriculas).
 */
public class Enrollment {

    private int id;
    private int studentId;
    private int gradeId;
    private String gradeName;
    private int academicYear;
    private EnrollmentStatus status;
    private LocalDate enrollmentDate;
    private LocalDate withdrawalDate;
    private String notes;

    public Enrollment() {
        this.status = EnrollmentStatus.ACTIVE;
    }

    public Enrollment(int studentId, int gradeId, int academicYear, LocalDate enrollmentDate) {
        this.studentId = studentId;
        this.gradeId = gradeId;
        setAcademicYear(academicYear);
        setEnrollmentDate(enrollmentDate);
        this.status = EnrollmentStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == EnrollmentStatus.ACTIVE;
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

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        if (academicYear < 2000 || academicYear > 2100) {
            throw new IllegalArgumentException("El año académico no es válido.");
        }
        this.academicYear = academicYear;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        if (enrollmentDate == null) {
            throw new IllegalArgumentException("La fecha de matrícula es obligatoria.");
        }
        this.enrollmentDate = enrollmentDate;
    }

    public LocalDate getWithdrawalDate() {
        return withdrawalDate;
    }

    public void setWithdrawalDate(LocalDate withdrawalDate) {
        this.withdrawalDate = withdrawalDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
