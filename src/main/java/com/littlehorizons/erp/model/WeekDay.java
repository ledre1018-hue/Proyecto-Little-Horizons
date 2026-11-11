package com.littlehorizons.erp.model;

/**
 * Values of the week day column. dbValue is the exact text stored in MySQL.
 */
public enum WeekDay {
    MONDAY("Lunes", "Lunes"),
    TUESDAY("Martes", "Martes"),
    WEDNESDAY("Miércoles", "Miércoles"),
    THURSDAY("Jueves", "Jueves"),
    FRIDAY("Viernes", "Viernes"),
    SATURDAY("Sábado", "Sábado");

    private final String dbValue;
    private final String label;

    WeekDay(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static WeekDay fromDbValue(String value) {
        for (WeekDay weekDay : values()) {
            if (weekDay.dbValue.equals(value)) {
                return weekDay;
            }
        }
        throw new IllegalArgumentException("Unknown week day: " + value);
    }
}
