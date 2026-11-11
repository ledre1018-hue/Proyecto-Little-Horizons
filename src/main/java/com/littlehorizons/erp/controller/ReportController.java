package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.controller.report.Report;
import com.littlehorizons.erp.controller.report.ReportTable;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.BusinessRuleException;
import com.littlehorizons.erp.util.LogFileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the registered reports and reads the last lines of the error log.
 */
public class ReportController {

    private final List<Report> reports;
    private final LogFileReader logFileReader;

    public ReportController(List<Report> reports, LogFileReader logFileReader) {
        this.reports = new ArrayList<>(reports);
        this.logFileReader = logFileReader;
    }

    public List<String> getReportTitles() {
        return reports.stream()
                .map(report -> report.getTitle())
                .toList();
    }

    /** reportNumber starts at 1, as shown in the menu. */
    public ReportTable generateReport(int reportNumber) {
        if (reportNumber < 1 || reportNumber > reports.size()) {
            throw new BusinessRuleException("El reporte seleccionado no existe.");
        }
        Report report = reports.get(reportNumber - 1);
        AppLogger.getInstance().info("ReportController.generateReport", "Report generated: " + report.getTitle());
        return report.generate();
    }

    public String getLogPath() {
        return logFileReader.getLogPath();
    }

    public List<String> getLastLogLines(int lineCount) {
        if (!logFileReader.logExists()) {
            throw new BusinessRuleException("Aún no existe el archivo de log (" + logFileReader.getLogPath() + ").");
        }
        try {
            return logFileReader.readLastLines(lineCount);
        } catch (IOException e) {
            AppLogger.getInstance().error("ReportController.getLastLogLines", "Cannot read the log file", e);
            throw new BusinessRuleException("No fue posible leer el archivo de log.");
        }
    }
}
