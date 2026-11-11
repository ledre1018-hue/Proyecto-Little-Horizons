package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.AcademicController;
import com.littlehorizons.erp.controller.AttendanceController;
import com.littlehorizons.erp.controller.EnrollmentController;
import com.littlehorizons.erp.model.AcademicPeriod;
import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.Attendance;
import com.littlehorizons.erp.model.AttendanceStatus;
import com.littlehorizons.erp.model.Grade;
import com.littlehorizons.erp.model.Subject;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.util.BusinessRuleException;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;
import com.littlehorizons.erp.util.TextFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Module 2: grades, automatic averages and attendance.
 */
public class AcademicView extends BaseView {

    private static final int MAX_OPTION = 8;

    private final AcademicController academicController;
    private final AttendanceController attendanceController;
    private final EnrollmentController enrollmentController;

    public AcademicView(AcademicController academicController, AttendanceController attendanceController,
                        EnrollmentController enrollmentController, ConsoleInput input, ConsolePrinter printer) {
        super(input, printer);
        this.academicController = academicController;
        this.attendanceController = attendanceController;
        this.enrollmentController = enrollmentController;
    }

    @Override
    public void show() {
        int option;
        do {
            printMenu();
            option = input.readMenuOption(MAX_OPTION);
            switch (option) {
                case 1 -> runAction(() -> registerGrade());
                case 2 -> runAction(() -> showHistory());
                case 3 -> runAction(() -> showSubjectAverages());
                case 4 -> runAction(() -> showPeriodAverage());
                case 5 -> runAction(() -> showCourseAverages());
                case 6 -> runAction(() -> registerAttendance());
                case 7 -> runAction(() -> showAttendance());
                case 8 -> runAction(() -> showConcerningAbsences());
                case CANCEL_OPTION -> printer.printInfo("Volviendo al menú principal...");
                default -> printUnknownOption();
            }
        } while (option != CANCEL_OPTION);
    }

    private void printMenu() {
        printer.printTitle("Gestión Académica");
        printer.printSubtitle("Calificaciones");
        printer.printOption(1, "Registrar nota");
        printer.printOption(2, "Ver historial académico de un estudiante");
        printer.printOption(3, "Promedio por materia de un estudiante");
        printer.printOption(4, "Promedio general de un estudiante en un período");
        printer.printOption(5, "Promedio general del curso en cada materia");
        printer.printSubtitle("Asistencia");
        printer.printOption(6, "Registrar asistencia");
        printer.printOption(7, "Ver asistencia de un estudiante");
        printer.printOption(8, "Reporte de inasistencias preocupantes");
        printBackOption();
    }

    private void registerGrade() {
        int studentId = readId("estudiante");
        int year = readYear();
        Subject subject = chooseSubject(studentId, year);
        TeacherAssignment teacher = chooseTeacher(subject.getId(), year);
        AcademicPeriod period = readOption("Período:", AcademicPeriod.values(), option -> option.getLabel());
        double value = input.readGrade("Nota");
        AcademicRecord record = new AcademicRecord(studentId, subject.getId(), teacher.getTeacherId(), year, period,
                value);
        record.setNotes(input.readOptionalText("Observaciones"));
        int recordId = academicController.registerGrade(record);
        printer.printSuccess("Nota " + TextFormat.grade(value) + " registrada con ID " + recordId + ".");
        if (record.isFailing()) {
            printer.printWarning("La nota es inferior a 3.0: el estudiante necesita apoyo en esta materia.");
        }
    }

    private void showHistory() {
        List<AcademicRecord> history = academicController.getHistory(readId("estudiante"));
        printer.printSubtitle("Historial académico");
        printer.printTable(new String[]{"Año", "Grado", "Materia", "Período", "Nota", "Observaciones"},
                new int[]{4, 10, 20, 7, 4, 28}, history, record -> new String[]{
                        String.valueOf(record.getAcademicYear()), record.getGradeName(), record.getSubjectName(),
                        record.getPeriod().getDbValue(), TextFormat.grade(record.getValue()),
                        TextFormat.orDash(record.getNotes())});
    }

    private void showSubjectAverages() {
        int studentId = readId("estudiante");
        int year = readYear();
        printAverages("Promedio por materia (períodos I a IV) - " + year,
                academicController.getSubjectAverages(studentId, year));
    }

    private void showPeriodAverage() {
        int studentId = readId("estudiante");
        int year = readYear();
        AcademicPeriod period = readOption("Período:", AcademicPeriod.values(), option -> option.getLabel());
        double average = academicController.getPeriodAverage(studentId, year, period);
        printer.printSuccess("Promedio general del período " + period.getDbValue() + " de " + year + ": "
                + TextFormat.average(average));
    }

    private void showCourseAverages() {
        Grade grade = input.readChoice("Curso (grado):", enrollmentController.listGrades(), option -> option.getName());
        int year = readYear();
        printAverages("Promedio del curso " + grade.getName() + " por materia - " + year,
                academicController.getCourseAverages(grade.getId(), year));
    }

    private void registerAttendance() {
        int studentId = readId("estudiante");
        LocalDate date = input.readDateOrDefault("Fecha de la clase", () -> LocalDate.now());
        Subject subject = chooseSubject(studentId, date.getYear());
        AttendanceStatus status = readOption("Estado de asistencia:", AttendanceStatus.values(),
                option -> option.getLabel());
        TeacherAssignment teacher = chooseTeacher(subject.getId(), date.getYear());
        Attendance attendance = new Attendance(studentId, subject.getId(), date, status);
        attendance.setRecordedBy(teacher.getTeacherId());
        attendance.setNotes(input.readOptionalText("Observaciones"));
        int attendanceId = attendanceController.registerAttendance(attendance);
        printer.printSuccess("Asistencia registrada con ID " + attendanceId + ".");
    }

    private void showAttendance() {
        List<Attendance> records = attendanceController.getAttendance(readId("estudiante"));
        printer.printTable(new String[]{"Fecha", "Materia", "Estado", "Observaciones"},
                new int[]{10, 20, 14, 34}, records, attendance -> new String[]{
                        TextFormat.date(attendance.getDate()), attendance.getSubjectName(),
                        attendance.getStatus().getLabel(), TextFormat.orDash(attendance.getNotes())});
    }

    private void showConcerningAbsences() {
        printer.printSubtitle("Estudiantes con más de " + AttendanceController.MAX_WEEKLY_ABSENCES
                + " ausencias en una semana");
        printer.printTable(new String[]{"ID", "Estudiante", "Semana (lunes)", "Ausencias"},
                new int[]{4, 32, 14, 9}, attendanceController.findConcerningAbsences(), alert -> new String[]{
                        String.valueOf(alert.getStudentId()), alert.getStudentName(),
                        TextFormat.date(alert.getWeekStart()), String.valueOf(alert.getAbsences())});
    }

    private Subject chooseSubject(int studentId, int year) {
        List<Subject> subjects = enrollmentController.getSubjectsForStudent(studentId, year);
        return input.readChoice("Materia:", subjects, option -> option.getCode() + " - " + option.getName());
    }

    private TeacherAssignment chooseTeacher(int subjectId, int year) {
        List<TeacherAssignment> teachers = academicController.getTeachersForSubject(subjectId, year);
        if (teachers.isEmpty()) {
            throw new BusinessRuleException("La materia no tiene profesores asignados en " + year + ".");
        }
        return input.readChoice("Profesor:", teachers,
                option -> option.getTeacherName() + " (sección " + option.getSection() + ")");
    }

    private void printAverages(String title, Map<String, Double> averages) {
        printer.printSubtitle(title);
        List<Map.Entry<String, Double>> rows = new ArrayList<>(averages.entrySet());
        printer.printTable(new String[]{"Materia", "Promedio", "Desempeño"}, new int[]{24, 8, 12}, rows,
                entry -> new String[]{entry.getKey(), TextFormat.average(entry.getValue()),
                        entry.getValue() < AcademicRecord.PASSING_GRADE ? "Bajo" : "Aprobado"});
    }
}
