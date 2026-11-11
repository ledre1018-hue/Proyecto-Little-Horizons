package com.littlehorizons.erp.model;

import java.math.BigDecimal;

/**
 * One line of an invoice. It only exists inside an Invoice (composition).
 */
public class InvoiceLine {

    private final String description;
    private final int quantity;
    private final BigDecimal unitPrice;

    InvoiceLine(String description, int quantity, BigDecimal unitPrice) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La descripción de la línea es obligatoria.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1.");
        }
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalArgumentException("El valor unitario no puede ser negativo.");
        }
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
