package com.littlehorizons.erp.model;

/**
 * Participation of a student in an event (table participantes_evento).
 */
public class EventParticipant {

    private int id;
    private int eventId;
    private String eventName;
    private int studentId;
    private String studentName;
    private ParticipationRole role;
    private CompetitionPosition position;
    private String notes;

    public EventParticipant() {
        this.role = ParticipationRole.PARTICIPANT;
    }

    public EventParticipant(int eventId, int studentId, ParticipationRole role, CompetitionPosition position) {
        this.eventId = eventId;
        this.studentId = studentId;
        setRole(role);
        this.position = position;
    }

    public boolean isPodium() {
        return position != null && position != CompetitionPosition.PARTICIPANT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public ParticipationRole getRole() {
        return role;
    }

    public void setRole(ParticipationRole role) {
        if (role == null) {
            throw new IllegalArgumentException("El rol de participación es obligatorio.");
        }
        this.role = role;
    }

    public CompetitionPosition getPosition() {
        return position;
    }

    public void setPosition(CompetitionPosition position) {
        this.position = position;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
