package com.littlehorizons.erp.model;

/**
 * Values of the loan status column. dbValue is the exact text stored in MySQL.
 */
public enum LoanStatus {
    ACTIVE("Activo", "Activo"),
    RETURNED("Devuelto", "Devuelto"),
    OVERDUE("Vencido", "Vencido");

    private final String dbValue;
    private final String label;

    LoanStatus(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static LoanStatus fromDbValue(String value) {
        for (LoanStatus loanStatus : values()) {
            if (loanStatus.dbValue.equals(value)) {
                return loanStatus;
            }
        }
        throw new IllegalArgumentException("Unknown loan status: " + value);
    }
}
