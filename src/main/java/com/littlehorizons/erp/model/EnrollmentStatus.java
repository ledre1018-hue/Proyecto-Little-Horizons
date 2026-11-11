package com.littlehorizons.erp.model;

/**
 * Values of the enrollment status column. dbValue is the exact text stored in MySQL.
 */
public enum EnrollmentStatus {
    ACTIVE("Activa", "Activa"),
    COMPLETED("Completada", "Completada"),
    WITHDRAWN("Retirada", "Retirada");

    private final String dbValue;
    private final String label;

    EnrollmentStatus(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static EnrollmentStatus fromDbValue(String value) {
        for (EnrollmentStatus enrollmentStatus : values()) {
            if (enrollmentStatus.dbValue.equals(value)) {
                return enrollmentStatus;
            }
        }
        throw new IllegalArgumentException("Unknown enrollment status: " + value);
    }
}
