package com.littlehorizons.erp.model;

/**
 * School grade, from Preescolar (level 0) to Once (level 11).
 */
public class Grade {

    private int id;
    private String name;
    private int level;

    public Grade() {
    }

    public Grade(int id, String name, int level) {
        this.id = id;
        setName(name);
        setLevel(level);
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
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del grado es obligatorio.");
        }
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level < 0 || level > 11) {
            throw new IllegalArgumentException("El nivel del grado debe estar entre 0 y 11.");
        }
        this.level = level;
    }
}
