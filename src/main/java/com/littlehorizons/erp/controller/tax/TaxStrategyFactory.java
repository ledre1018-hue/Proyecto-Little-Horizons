package com.littlehorizons.erp.controller.tax;

import java.util.HashMap;
import java.util.Map;

/**
 * Chooses the tax strategy for a payment concept code.
 * Simulated rule: UNIFORME pays 19% VAT; every other concept is educational and exempt.
 * A new tax only needs a new TaxStrategy class and one line in the constructor.
 */
public class TaxStrategyFactory {

    private static final String UNIFORM_CODE = "UNIFORME";

    private final Map<String, TaxStrategy> strategiesByConceptCode;
    private final TaxStrategy defaultStrategy;

    public TaxStrategyFactory() {
        this.strategiesByConceptCode = new HashMap<>();
        this.defaultStrategy = new VatExemptTaxStrategy();
        strategiesByConceptCode.put(UNIFORM_CODE, new IncludedVatTaxStrategy());
    }

    public TaxStrategy forConcept(String conceptCode) {
        return strategiesByConceptCode.getOrDefault(conceptCode, defaultStrategy);
    }
}
