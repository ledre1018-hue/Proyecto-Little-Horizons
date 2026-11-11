package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.ReportController;
import com.littlehorizons.erp.controller.report.ReportTable;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;

import java.util.List;

/**
 * Module 7: reports and statistics, plus the last lines of the error log.
 */
public class ReportView extends BaseView {

    private static final int LOG_LINES = 20;

    private final ReportController reportController;

    public ReportView(ReportController reportController, ConsoleInput input, ConsolePrinter printer) {
        super(input, printer);
        this.reportController = reportController;
    }

    @Override
    public void show() {
        List<String> titles = reportController.getReportTitles();
        int logOption = titles.size() + 1;
        int option;
        do {
            printMenu(titles, logOption);
            option = input.readMenuOption(logOption);
            int selected = option;
            if (option == CANCEL_OPTION) {
                printer.printInfo("Volviendo al menú principal...");
            } else if (option == logOption) {
                runAction(() -> showLastLogLines());
            } else {
                runAction(() -> printReport(reportController.generateReport(selected)));
            }
        } while (option != CANCEL_OPTION);
    }

    private void printMenu(List<String> titles, int logOption) {
        printer.printTitle("Reportes y Estadísticas");
        for (int index = 0; index < titles.size(); index++) {
            printer.printOption(index + 1, titles.get(index));
        }
        printer.printSubtitle("Archivos");
        printer.printOption(logOption, "Ver últimas " + LOG_LINES + " líneas del log de errores");
        printBackOption();
    }

    private void printReport(ReportTable report) {
        printer.printSubtitle(report.getTitle());
        printer.printTable(report.getHeaders(), report.getWidths(), report.getRows(), row -> row);
    }

    private void showLastLogLines() {
        List<String> lines = reportController.getLastLogLines(LOG_LINES);
        printer.printSubtitle("Últimas líneas de " + reportController.getLogPath());
        if (lines.isEmpty()) {
            printer.printInfo("El archivo de log está vacío.");
        }
        lines.forEach(line -> printer.printLine(line));
    }
}
