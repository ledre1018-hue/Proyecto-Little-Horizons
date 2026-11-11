package com.littlehorizons.erp.model;

/**
 * Values of the attendance status column. dbValue is the exact text stored in MySQL.
 */
public enum AttendanceStatus {
    PRESENT("Presente", "Presente"),
    ABSENT("Ausente", "Ausente"),
    LATE("Tardanza", "Tardanza"),
    MEDICAL_EXCUSE("Excusa Médica", "Excusa Médica");

    private final String dbValue;
    private final String label;

    AttendanceStatus(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static AttendanceStatus fromDbValue(String value) {
        for (AttendanceStatus attendanceStatus : values()) {
            if (attendanceStatus.dbValue.equals(value)) {
                return attendanceStatus;
            }
        }
        throw new IllegalArgumentException("Unknown attendance status: " + value);
    }
}
