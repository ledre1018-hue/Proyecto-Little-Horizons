package com.littlehorizons.erp.controller.report;

/**
 * Contract of every report. A new report is a new class; nothing else has to change
 * except registering it in the ControllerFactory list (Open/Closed principle).
 */
public interface Report {

    String getTitle();

    ReportTable generate();
}
