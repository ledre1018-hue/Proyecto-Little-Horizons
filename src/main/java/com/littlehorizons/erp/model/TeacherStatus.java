package com.littlehorizons.erp.model;

/**
 * Values of the teacher status column. dbValue is the exact text stored in MySQL.
 */
public enum TeacherStatus {
    ACTIVE("Activo", "Activo"),
    INACTIVE("Inactivo", "Inactivo"),
    ON_LEAVE("Licencia", "Licencia");

    private final String dbValue;
    private final String label;

    TeacherStatus(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static TeacherStatus fromDbValue(String value) {
        for (TeacherStatus teacherStatus : values()) {
            if (teacherStatus.dbValue.equals(value)) {
                return teacherStatus;
            }
        }
        throw new IllegalArgumentException("Unknown teacher status: " + value);
    }
}
