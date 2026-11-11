package com.littlehorizons.erp.model;

/**
 * Values of the gender column. dbValue is the exact text stored in MySQL.
 */
public enum Gender {
    MALE("M", "Masculino"),
    FEMALE("F", "Femenino"),
    OTHER("Otro", "Otro");

    private final String dbValue;
    private final String label;

    Gender(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static Gender fromDbValue(String value) {
        for (Gender gender : values()) {
            if (gender.dbValue.equals(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender: " + value);
    }
}
