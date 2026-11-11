package com.littlehorizons.erp.controller.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * VAT of 19% already included in the amount paid (used for goods like uniforms).
 * tax = total - total / 1.19
 */
public class IncludedVatTaxStrategy implements TaxStrategy {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.19");

    @Override
    public String getDescription() {
        return "IVA 19% incluido";
    }

    @Override
    public BigDecimal getRate() {
        return VAT_RATE;
    }

    @Override
    public BigDecimal calculateIncludedTax(BigDecimal totalPaid) {
        BigDecimal baseAmount = totalPaid.divide(BigDecimal.ONE.add(VAT_RATE), 2, RoundingMode.HALF_UP);
        return totalPaid.subtract(baseAmount);
    }
}
