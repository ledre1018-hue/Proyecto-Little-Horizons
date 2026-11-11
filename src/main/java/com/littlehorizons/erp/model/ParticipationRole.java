package com.littlehorizons.erp.model;

/**
 * Values of the participation role column. dbValue is the exact text stored in MySQL.
 */
public enum ParticipationRole {
    PARTICIPANT("Participante", "Participante"),
    COMPETITOR("Competidor", "Competidor"),
    ORGANIZER("Organizador", "Organizador"),
    ATTENDEE("Asistente", "Asistente");

    private final String dbValue;
    private final String label;

    ParticipationRole(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static ParticipationRole fromDbValue(String value) {
        for (ParticipationRole participationRole : values()) {
            if (participationRole.dbValue.equals(value)) {
                return participationRole;
            }
        }
        throw new IllegalArgumentException("Unknown participation role: " + value);
    }
}
