package com.littlehorizons.erp.model;

/**
 * Values of the membership status column. dbValue is the exact text stored in MySQL.
 */
public enum MembershipStatus {
    ACTIVE("Activa", "Activa"),
    WITHDRAWN("Retirada", "Retirada");

    private final String dbValue;
    private final String label;

    MembershipStatus(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static MembershipStatus fromDbValue(String value) {
        for (MembershipStatus membershipStatus : values()) {
            if (membershipStatus.dbValue.equals(value)) {
                return membershipStatus;
            }
        }
        throw new IllegalArgumentException("Unknown membership status: " + value);
    }
}
