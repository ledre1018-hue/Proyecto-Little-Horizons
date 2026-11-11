package com.littlehorizons.erp.model;

/**
 * Values of the student status column. dbValue is the exact text stored in MySQL.
 */
public enum StudentStatus {
    ACTIVE("Activo", "Activo"),
    GRADUATED("Graduado", "Graduado"),
    WITHDRAWN("Retirado", "Retirado"),
    SUSPENDED("Suspendido", "Suspendido");

    private final String dbValue;
    private final String label;

    StudentStatus(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static StudentStatus fromDbValue(String value) {
        for (StudentStatus studentStatus : values()) {
            if (studentStatus.dbValue.equals(value)) {
                return studentStatus;
            }
        }
        throw new IllegalArgumentException("Unknown student status: " + value);
    }
}
