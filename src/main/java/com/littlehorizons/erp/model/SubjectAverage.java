package com.littlehorizons.erp.model;

import java.math.BigDecimal;

/**
 * General average of one subject, using every grade of every student and period.
 * Result of a calculation, not a table. The average is kept with one decimal.
 */
public class SubjectAverage {

    private final int subjectId;
    private final String subjectCode;
    private final String subjectName;
    private final BigDecimal average;

    public SubjectAverage(int subjectId, String subjectCode, String subjectName, BigDecimal average) {
        if (average == null || average.scale() != 1) {
            throw new IllegalArgumentException("El promedio debe tener exactamente un decimal.");
        }
        if (average.compareTo(BigDecimal.valueOf(AcademicRecord.MIN_GRADE)) < 0
                || average.compareTo(BigDecimal.valueOf(AcademicRecord.MAX_GRADE)) > 0) {
            throw new IllegalArgumentException("El promedio debe estar entre 0.0 y 5.0.");
        }
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.average = average;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public BigDecimal getAverage() {
        return average;
    }
}
