package com.littlehorizons.erp.model;

/**
 * Category of a resource, e.g. Libro or Equipo de Laboratorio (table categorias_recurso).
 */
public class ResourceCategory {

    private int id;
    private String name;
    private String description;

    public ResourceCategory() {
    }

    public ResourceCategory(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
