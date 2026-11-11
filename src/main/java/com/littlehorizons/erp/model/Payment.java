package com.littlehorizons.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A payment made by a guardian for a student (table pagos).
 */
public class Payment {

    private int id;
    private int studentId;
    private String studentName;
    private Integer guardianId;
    private String guardianName;
    private int conceptId;
    private String conceptName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod method;
    private String receiptNumber;
    private Integer academicYear;
    private PaymentPeriod period;
    private String notes;

    public Payment() {
    }

    public Payment(int studentId, int guardianId, int conceptId, BigDecimal amount, LocalDate paymentDate,
                   PaymentMethod method) {
        this.studentId = studentId;
        this.guardianId = guardianId;
        this.conceptId = conceptId;
        setAmount(amount);
        setPaymentDate(paymentDate);
        setMethod(method);
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

    public Integer getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(Integer guardianId) {
        this.guardianId = guardianId;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("El monto pagado debe ser mayor que cero.");
        }
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        if (paymentDate == null || paymentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de pago es obligatoria y no puede ser futura.");
        }
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        this.method = method;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public PaymentPeriod getPeriod() {
        return period;
    }

    public void setPeriod(PaymentPeriod period) {
        this.period = period;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
