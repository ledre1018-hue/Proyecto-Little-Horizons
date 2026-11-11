package com.littlehorizons.erp.model;

import java.math.BigDecimal;

/**
 * Item of the payment catalog, for example tuition or uniform (table conceptos_pago).
 */
public class PaymentConcept {

    private int id;
    private String code;
    private String name;
    private String description;
    private BigDecimal baseAmount;
    private boolean recurring;
    private boolean active;

    public PaymentConcept() {
        this.active = true;
    }

    public PaymentConcept(String code, String name, BigDecimal baseAmount, boolean recurring) {
        setCode(code);
        setName(name);
        setBaseAmount(baseAmount);
        this.recurring = recurring;
        this.active = true;
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
            throw new IllegalArgumentException("El código del concepto es obligatorio (máximo 20 caracteres).");
        }
        this.code = code.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del concepto es obligatorio.");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        if (baseAmount == null || baseAmount.signum() <= 0) {
            throw new IllegalArgumentException("El valor base debe ser mayor que cero.");
        }
        this.baseAmount = baseAmount;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
