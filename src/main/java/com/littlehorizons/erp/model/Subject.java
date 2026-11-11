package com.littlehorizons.erp.model;

/**
 * Subject taught in one grade (table materias).
 */
public class Subject {

    private int id;
    private String code;
    private String name;
    private String description;
    private int gradeId;
    private String gradeName;
    private int weeklyHours;
    private boolean active;

    public Subject() {
        this.weeklyHours = 2;
        this.active = true;
    }

    public Subject(String code, String name, int gradeId, int weeklyHours) {
        setCode(code);
        setName(name);
        this.gradeId = gradeId;
        setWeeklyHours(weeklyHours);
        this.active = true;
    }

    public String getDisplayName() {
        return name + " (" + gradeName + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code == null || code.isBlank() || code.trim().length() > 20) {
            throw new IllegalArgumentException("El código de la materia es obligatorio (máximo 20 caracteres).");
        }
        this.code = code.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la materia es obligatorio.");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        if (weeklyHours < 1 || weeklyHours > 40) {
            throw new IllegalArgumentException("Las horas semanales deben estar entre 1 y 40.");
        }
        this.weeklyHours = weeklyHours;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
