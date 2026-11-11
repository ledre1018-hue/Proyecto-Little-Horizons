package com.littlehorizons.erp.controller.tax;

import java.math.BigDecimal;

/**
 * Strategy pattern: each implementation knows how to calculate the tax of a payment.
 * The tax is already included in the amount paid.
 */
public interface TaxStrategy {

    /** Text printed on the invoice, for example "IVA 19% incluido". */
    String getDescription();

    BigDecimal getRate();

    /** Returns the tax that is contained in the total paid. */
    BigDecimal calculateIncludedTax(BigDecimal totalPaid);
}
