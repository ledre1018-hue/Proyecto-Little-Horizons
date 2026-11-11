package com.littlehorizons.erp.controller.report;

import com.littlehorizons.erp.controller.LoanController;
import com.littlehorizons.erp.util.TextFormat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class OverdueLoansReport implements Report {

    private final LoanController loanController;

    public OverdueLoansReport(LoanController loanController) {
        this.loanController = loanController;
    }

    @Override
    public String getTitle() {
        return "Préstamos vencidos";
    }

    @Override
    public ReportTable generate() {
        ReportTable table = new ReportTable(getTitle(), new String[]{"ID", "Tipo", "Prestatario", "Recurso",
                "Debía volver", "Días"}, new int[]{4, 10, 24, 30, 12, 5});
        LocalDate today = LocalDate.now();
        loanController.findOverdueLoans().forEach(loan -> table.addRow(String.valueOf(loan.getId()),
                loan.getBorrowerType(), loan.getBorrowerName(), loan.getResourceName(),
                TextFormat.date(loan.getDueDate()), String.valueOf(ChronoUnit.DAYS.between(loan.getDueDate(), today))));
        return table;
    }
}
