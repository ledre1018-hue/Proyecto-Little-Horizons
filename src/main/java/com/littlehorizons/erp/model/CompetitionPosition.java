package com.littlehorizons.erp.model;

/**
 * Values of the competition position column. dbValue is the exact text stored in MySQL.
 */
public enum CompetitionPosition {
    FIRST_PLACE("1er Lugar", "1er Lugar"),
    SECOND_PLACE("2do Lugar", "2do Lugar"),
    THIRD_PLACE("3er Lugar", "3er Lugar"),
    PARTICIPANT("Participante", "Participante");

    private final String dbValue;
    private final String label;

    CompetitionPosition(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

    public static CompetitionPosition fromDbValue(String value) {
        for (CompetitionPosition competitionPosition : values()) {
            if (competitionPosition.dbValue.equals(value)) {
                return competitionPosition;
            }
        }
        throw new IllegalArgumentException("Unknown competition position: " + value);
    }
}
