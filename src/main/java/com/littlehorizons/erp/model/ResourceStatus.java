package com.littlehorizons.erp.model;

/**
 * Values of the resource status column. dbValue is the exact text stored in MySQL.
 */
public enum ResourceStatus {
    AVAILABLE("Disponible", "Disponible"),
    BORROWED("Prestado", "Prestado"),
    MAINTENANCE("En Mantenimiento", "En Mantenimiento"),
    RETIRED("Dado de Baja", "Dado de Baja");

    private final String dbValue;
    private final String label;

    ResourceStatus(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static ResourceStatus fromDbValue(String value) {
        for (ResourceStatus resourceStatus : values()) {
            if (resourceStatus.dbValue.equals(value)) {
                return resourceStatus;
            }
        }
        throw new IllegalArgumentException("Unknown resource status: " + value);
    }
}
