package com.littlehorizons.erp.model;

/**
 * Values of the academic period column. dbValue is the exact text stored in MySQL.
 */
public enum AcademicPeriod {
    FIRST("I", "Período I"),
    SECOND("II", "Período II"),
    THIRD("III", "Período III"),
    FOURTH("IV", "Período IV"),
    FINAL("Final", "Nota final");

    private final String dbValue;
    private final String label;

    AcademicPeriod(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static AcademicPeriod fromDbValue(String value) {
        for (AcademicPeriod academicPeriod : values()) {
            if (academicPeriod.dbValue.equals(value)) {
                return academicPeriod;
            }
        }
        throw new IllegalArgumentException("Unknown academic period: " + value);
    }
}
