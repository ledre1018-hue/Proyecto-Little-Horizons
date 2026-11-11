package com.littlehorizons.erp.controller.report;

import com.littlehorizons.erp.controller.PaymentController;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.util.TextFormat;

import java.util.List;
import java.util.Map;

public class PaymentsByMethodReport implements Report {

    private final PaymentController paymentController;

    public PaymentsByMethodReport(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    @Override
    public String getTitle() {
        return "Resumen de pagos por método de pago";
    }

    @Override
    public ReportTable generate() {
        ReportTable table = new ReportTable(getTitle(), new String[]{"Método de pago", "Cantidad", "Total recaudado"},
                new int[]{20, 9, 18});
        for (Map.Entry<String, List<Payment>> entry : paymentController.groupPaymentsByMethod().entrySet()) {
            table.addRow(entry.getKey(), String.valueOf(entry.getValue().size()),
                    TextFormat.money(paymentController.sumAmounts(entry.getValue())));
        }
        return table;
    }
}
