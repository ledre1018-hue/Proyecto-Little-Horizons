package com.littlehorizons.erp.controller.report;

import com.littlehorizons.erp.controller.AcademicController;
import com.littlehorizons.erp.util.TextFormat;

import java.time.LocalDate;

public class TopAveragesReport implements Report {

    private final AcademicController academicController;

    public TopAveragesReport(AcademicController academicController) {
        this.academicController = academicController;
    }

    @Override
    public String getTitle() {
        return "Mejores promedios por curso y materia";
    }

    @Override
    public ReportTable generate() {
        int year = LocalDate.now().getYear();
        ReportTable table = new ReportTable(getTitle() + " - " + year, new String[]{"Curso", "Materia", "ID",
                "Mejor estudiante", "Promedio"}, new int[]{11, 20, 4, 28, 8});
        academicController.getTopAverages(year).forEach(average -> table.addRow(average.getGradeName(),
                average.getSubjectName(), String.valueOf(average.getStudentId()), average.getStudentName(),
                TextFormat.average(average.getAverage())));
        return table;
    }
}
