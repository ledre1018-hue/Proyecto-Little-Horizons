package com.littlehorizons.erp.controller.report;

import com.littlehorizons.erp.controller.AttendanceController;
import com.littlehorizons.erp.util.TextFormat;

public class ConcerningAbsencesReport implements Report {

    private final AttendanceController attendanceController;

    public ConcerningAbsencesReport(AttendanceController attendanceController) {
        this.attendanceController = attendanceController;
    }

    @Override
    public String getTitle() {
        return "Inasistencias preocupantes (más de " + AttendanceController.MAX_WEEKLY_ABSENCES
                + " ausencias en una semana)";
    }

    @Override
    public ReportTable generate() {
        ReportTable table = new ReportTable(getTitle(), new String[]{"ID", "Estudiante", "Semana desde", "Semana hasta",
                "Ausencias"}, new int[]{4, 32, 12, 12, 9});
        attendanceController.findConcerningAbsences().forEach(alert -> table.addRow(
                String.valueOf(alert.getStudentId()), alert.getStudentName(), TextFormat.date(alert.getWeekStart()),
                TextFormat.date(alert.getWeekEnd()), String.valueOf(alert.getAbsences())));
        return table;
    }
}
