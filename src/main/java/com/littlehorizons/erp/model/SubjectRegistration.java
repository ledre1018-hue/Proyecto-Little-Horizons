package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Registration of a student in a subject for one period (table inscripciones_materia).
 */
public class SubjectRegistration {

    private int id;
    private int studentId;
    private int subjectId;
    private String subjectName;
    private int academicYear;
    private AcademicPeriod period;
    private String section;
    private LocalDate registrationDate;
    private EnrollmentStatus status;

    public SubjectRegistration() {
        this.status = EnrollmentStatus.ACTIVE;
    }

    public SubjectRegistration(int studentId, int subjectId, int academicYear, AcademicPeriod period,
                               LocalDate registrationDate) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.academicYear = academicYear;
        setPeriod(period);
        this.registrationDate = registrationDate;
        this.section = "A";
        this.status = EnrollmentStatus.ACTIVE;
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
        if (period == null || period == AcademicPeriod.FINAL) {
            throw new IllegalArgumentException("La inscripción debe ser en un período I, II, III o IV.");
        }
        this.period = period;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }
}
