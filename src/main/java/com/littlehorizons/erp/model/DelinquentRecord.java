package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * A recurring concept that a student has not paid for more than one month.
 * Result of a calculation, not a table.
 */
public class DelinquentRecord {

    private final int studentId;
    private final String studentName;
    private final String conceptName;
    private final LocalDate lastPaymentDate;
    private final long daysWithoutPayment;

    public DelinquentRecord(int studentId, String studentName, String conceptName, LocalDate lastPaymentDate,
                            long daysWithoutPayment) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.conceptName = conceptName;
        this.lastPaymentDate = lastPaymentDate;
        this.daysWithoutPayment = daysWithoutPayment;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getConceptName() {
        return conceptName;
    }

    /** Null when the student never paid this concept. */
    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public long getDaysWithoutPayment() {
        return daysWithoutPayment;
    }
}
