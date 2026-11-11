package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * School event such as a science fair or a sports day (table eventos).
 */
public class SchoolEvent {

    private int id;
    private String name;
    private String description;
    private EventType type;
    private LocalDate eventDate;
    private String place;
    private boolean competition;
    private Integer organizerId;
    private boolean active;

    public SchoolEvent() {
        this.active = true;
    }

    public SchoolEvent(String name, EventType type, LocalDate eventDate, boolean competition) {
        setName(name);
        setType(type);
        setEventDate(eventDate);
        this.competition = competition;
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
        if (name == null || name.isBlank() || name.trim().length() > 150) {
            throw new IllegalArgumentException("El nombre del evento es obligatorio (máximo 150 caracteres).");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        if (type == null) {
            throw new IllegalArgumentException("El tipo de evento es obligatorio.");
        }
        this.type = type;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        if (eventDate == null) {
            throw new IllegalArgumentException("La fecha del evento es obligatoria.");
        }
        this.eventDate = eventDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean hasCompetition() {
        return competition;
    }

    public void setCompetition(boolean competition) {
        this.competition = competition;
    }

    public Integer getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Integer organizerId) {
        this.organizerId = organizerId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
