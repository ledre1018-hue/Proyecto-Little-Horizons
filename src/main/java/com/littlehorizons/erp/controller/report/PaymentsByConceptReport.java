package com.littlehorizons.erp.controller.report;

import com.littlehorizons.erp.controller.PaymentController;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.util.TextFormat;

import java.util.List;
import java.util.Map;

public class PaymentsByConceptReport implements Report {

    private final PaymentController paymentController;

    public PaymentsByConceptReport(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    @Override
    public String getTitle() {
        return "Resumen de pagos por concepto";
    }

    @Override
    public ReportTable generate() {
        ReportTable table = new ReportTable(getTitle(), new String[]{"Concepto", "Cantidad", "Total recaudado"},
                new int[]{28, 9, 18});
        for (Map.Entry<String, List<Payment>> entry : paymentController.groupPaymentsByConcept().entrySet()) {
            table.addRow(entry.getKey(), String.valueOf(entry.getValue().size()),
                    TextFormat.money(paymentController.sumAmounts(entry.getValue())));
        }
        return table;
    }
}
