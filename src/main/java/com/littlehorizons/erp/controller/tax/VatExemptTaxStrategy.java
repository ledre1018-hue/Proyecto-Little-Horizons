package com.littlehorizons.erp.controller.tax;

import java.math.BigDecimal;

/**
 * Educational services are exempt from VAT (IVA 0%).
 */
public class VatExemptTaxStrategy implements TaxStrategy {

    @Override
    public String getDescription() {
        return "Exento de IVA (servicio educativo)";
    }

    @Override
    public BigDecimal getRate() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateIncludedTax(BigDecimal totalPaid) {
        return BigDecimal.ZERO;
    }
}
