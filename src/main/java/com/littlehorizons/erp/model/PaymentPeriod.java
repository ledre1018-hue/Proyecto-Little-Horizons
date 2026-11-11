package com.littlehorizons.erp.model;

/**
 * Values of the payment period column. dbValue is the exact text stored in MySQL.
 */
public enum PaymentPeriod {
    FIRST("I", "Período I"),
    SECOND("II", "Período II"),
    THIRD("III", "Período III"),
    FOURTH("IV", "Período IV"),
    ANNUAL("Anual", "Anual");

    private final String dbValue;
    private final String label;

    PaymentPeriod(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static PaymentPeriod fromDbValue(String value) {
        for (PaymentPeriod paymentPeriod : values()) {
            if (paymentPeriod.dbValue.equals(value)) {
                return paymentPeriod;
            }
        }
        throw new IllegalArgumentException("Unknown payment period: " + value);
    }
}
