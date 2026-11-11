package com.littlehorizons.erp.controller.report;

import com.littlehorizons.erp.controller.AcademicController;
import com.littlehorizons.erp.util.TextFormat;

import java.time.LocalDate;

public class StudentsNeedingSupportReport implements Report {

    private final AcademicController academicController;

    public StudentsNeedingSupportReport(AcademicController academicController) {
        this.academicController = academicController;
    }

    @Override
    public String getTitle() {
        return "Estudiantes que necesitan apoyo (promedio menor a 3.0) por materia";
    }

    @Override
    public ReportTable generate() {
        int year = LocalDate.now().getYear();
        ReportTable table = new ReportTable(getTitle() + " - " + year, new String[]{"Curso", "Materia", "ID",
                "Estudiante", "Promedio"}, new int[]{11, 20, 4, 28, 8});
        academicController.getStudentsNeedingSupport(year).forEach(average -> table.addRow(average.getGradeName(),
                average.getSubjectName(), String.valueOf(average.getStudentId()), average.getStudentName(),
                TextFormat.average(average.getAverage())));
        return table;
    }
}
