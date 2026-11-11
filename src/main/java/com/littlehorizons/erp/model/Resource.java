package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Item that can be lent: book, lab equipment, sports equipment, device (table recursos).
 */
public class Resource {

    private int id;
    private String code;
    private String name;
    private int categoryId;
    private String categoryName;
    private String description;
    private LocalDate acquisitionDate;
    private String location;
    private ResourceStatus status;

    public Resource() {
        this.status = ResourceStatus.AVAILABLE;
    }

    public Resource(String code, String name, int categoryId, String location) {
        setCode(code);
        setName(name);
        this.categoryId = categoryId;
        this.location = location;
        this.status = ResourceStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return status == ResourceStatus.AVAILABLE;
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
        if (code == null || code.isBlank() || code.trim().length() > 50) {
            throw new IllegalArgumentException("El código del recurso es obligatorio (máximo 50 caracteres).");
        }
        this.code = code.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank() || name.trim().length() > 150) {
            throw new IllegalArgumentException("El nombre del recurso es obligatorio (máximo 150 caracteres).");
        }
        this.name = name.trim();
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    }
}
