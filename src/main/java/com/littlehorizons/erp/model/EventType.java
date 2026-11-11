package com.littlehorizons.erp.model;

/**
 * Values of the event type column. dbValue is the exact text stored in MySQL.
 */
public enum EventType {
    ACADEMIC("Académico", "Académico"),
    CULTURAL("Cultural", "Cultural"),
    SPORTS("Deportivo", "Deportivo"),
    SOCIAL("Social", "Social"),
    SCIENTIFIC("Científico", "Científico");

    private final String dbValue;
    private final String label;

    EventType(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static EventType fromDbValue(String value) {
        for (EventType eventType : values()) {
            if (eventType.dbValue.equals(value)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + value);
    }
}
