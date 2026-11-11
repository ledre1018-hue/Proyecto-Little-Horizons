package com.littlehorizons.erp.model;

/**
 * Extracurricular club with a maximum number of members (table clubes).
 */
public class Club {

    private int id;
    private String name;
    private String description;
    private Integer supervisorId;
    private String supervisorName;
    private int maxCapacity;
    private String meetingSchedule;
    private boolean active;

    public Club() {
        this.maxCapacity = 30;
        this.active = true;
    }

    public Club(String name, String description, Integer supervisorId, int maxCapacity) {
        setName(name);
        this.description = description;
        this.supervisorId = supervisorId;
        setMaxCapacity(maxCapacity);
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank() || name.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre del club es obligatorio (máximo 100 caracteres).");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Integer supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity < 1) {
            throw new IllegalArgumentException("El cupo máximo debe ser mayor que cero.");
        }
        this.maxCapacity = maxCapacity;
    }

    public String getMeetingSchedule() {
        return meetingSchedule;
    }

    public void setMeetingSchedule(String meetingSchedule) {
        this.meetingSchedule = meetingSchedule;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
