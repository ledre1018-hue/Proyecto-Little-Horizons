package com.littlehorizons.erp.model;

import java.math.BigDecimal;

/**
 * Custom price of a concept for one student and year, e.g. sibling discount
 * (table valores_concepto_estudiante).
 */
public class StudentConceptPrice {

    private int id;
    private int studentId;
    private String studentName;
    private int conceptId;
    private String conceptName;
    private int academicYear;
    private BigDecimal customAmount;
    private String reason;

    public StudentConceptPrice() {
    }

    public StudentConceptPrice(int studentId, int conceptId, int academicYear, BigDecimal customAmount,
                               String reason) {
        this.studentId = studentId;
        this.conceptId = conceptId;
        this.academicYear = academicYear;
        setCustomAmount(customAmount);
        setReason(reason);
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

    public int getConceptId() {
        return conceptId;
    }

    public void setConceptId(int conceptId) {
        this.conceptId = conceptId;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public BigDecimal getCustomAmount() {
        return customAmount;
    }

    public void setCustomAmount(BigDecimal customAmount) {
        if (customAmount == null || customAmount.signum() < 0) {
            throw new IllegalArgumentException("El valor personalizado no puede ser negativo.");
        }
        this.customAmount = customAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("El motivo del valor personalizado es obligatorio.");
        }
        this.reason = reason.trim();
    }
}
