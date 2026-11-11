package com.littlehorizons.erp.model;

/**
 * Values of the payment method column. dbValue is the exact text stored in MySQL.
 */
public enum PaymentMethod {
    CASH("Efectivo", "Efectivo"),
    TRANSFER("Transferencia", "Transferencia"),
    DEBIT_CARD("Tarjeta Débito", "Tarjeta Débito"),
    CREDIT_CARD("Tarjeta Crédito", "Tarjeta Crédito"),
    CHECK("Cheque", "Cheque");

    private final String dbValue;
    private final String label;

    PaymentMethod(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static PaymentMethod fromDbValue(String value) {
        for (PaymentMethod paymentMethod : values()) {
            if (paymentMethod.dbValue.equals(value)) {
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + value);
    }
}
