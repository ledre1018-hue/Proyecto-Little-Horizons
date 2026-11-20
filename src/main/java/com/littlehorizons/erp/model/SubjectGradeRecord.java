package com.littlehorizons.erp.model;

/**
 * One grade (nota) together with the data of its subject.
 * Read-only row used by the academic performance report (registros_academicos JOIN materias).
 */
public class SubjectGradeRecord {

    private final int recordId;
    private final double value;
    private final AcademicPeriod period;
    private final int subjectId;
    private final String subjectCode;
    private final String subjectName;

    public SubjectGradeRecord(int recordId, double value, AcademicPeriod period, int subjectId,
                              String subjectCode, String subjectName) {
        if (value < AcademicRecord.MIN_GRADE || value > AcademicRecord.MAX_GRADE) {
            throw new IllegalArgumentException("La nota debe estar entre 0.0 y 5.0.");
        }
        if (period == null) {
            throw new IllegalArgumentException("El período académico es obligatorio.");
        }
        if (subjectCode == null || subjectCode.isBlank() || subjectName == null || subjectName.isBlank()) {
            throw new IllegalArgumentException("El código y el nombre de la materia son obligatorios.");
        }
        this.recordId = recordId;
        this.value = value;
        this.period = period;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }

    public int getRecordId() {
        return recordId;
    }

    public double getValue() {
        return value;
    }

    public AcademicPeriod getPeriod() {
        return period;
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
}
