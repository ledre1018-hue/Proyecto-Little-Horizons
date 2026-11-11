package com.littlehorizons.erp.controller.report;

import com.littlehorizons.erp.controller.PaymentController;
import com.littlehorizons.erp.model.DelinquentRecord;

import java.time.LocalDate;

public class DelinquentStudentsReport implements Report {

    private final PaymentController paymentController;

    public DelinquentStudentsReport(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    @Override
    public String getTitle() {
        return "Estudiantes morosos (concepto recurrente sin pagar hace más de un mes)";
    }

    @Override
    public ReportTable generate() {
        ReportTable table = new ReportTable(getTitle(), new String[]{"ID", "Estudiante", "Concepto", "Último pago",
                "Días"}, new int[]{4, 30, 26, 12, 5});
        for (DelinquentRecord record : paymentController.findDelinquentStudents(LocalDate.now())) {
            String lastPayment = record.getLastPaymentDate() == null ? "Sin pagos"
                    : record.getLastPaymentDate().toString();
            table.addRow(String.valueOf(record.getStudentId()), record.getStudentName(), record.getConceptName(),
                    lastPayment, String.valueOf(record.getDaysWithoutPayment()));
        }
        return table;
    }
}
