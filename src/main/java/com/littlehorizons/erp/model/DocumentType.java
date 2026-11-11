package com.littlehorizons.erp.model;

/**
 * Values of the document type column. dbValue is the exact text stored in MySQL.
 */
public enum DocumentType {
    TI("TI", "Tarjeta de Identidad"),
    CC("CC", "Cédula de Ciudadanía"),
    CE("CE", "Cédula de Extranjería"),
    PASSPORT("Pasaporte", "Pasaporte");

    private final String dbValue;
    private final String label;

    DocumentType(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static DocumentType fromDbValue(String value) {
        for (DocumentType documentType : values()) {
            if (documentType.dbValue.equals(value)) {
                return documentType;
            }
        }
        throw new IllegalArgumentException("Unknown document type: " + value);
    }
}
