package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.ControllerFactory;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;

/**
 * Main menu of the application. Each option opens the view of one module.
 */
public class MainMenuView {

    private static final int BOX_WIDTH = 46;
    private static final int EXIT_OPTION = 8;

    private final ConsoleInput input;
    private final ConsolePrinter printer;
    private final BaseView studentView;
    private final BaseView academicView;
    private final BaseView teacherView;
    private final BaseView extracurricularView;
    private final BaseView financeView;
    private final BaseView resourceView;
    private final BaseView reportView;

    public MainMenuView(ControllerFactory controllers, ConsoleInput input, ConsolePrinter printer) {
        this.input = input;
        this.printer = printer;
        this.studentView = new StudentView(controllers.getStudentController(), controllers.getGuardianController(),
                controllers.getEnrollmentController(), input, printer);
        this.academicView = new AcademicView(controllers.getAcademicController(),
                controllers.getAttendanceController(), controllers.getEnrollmentController(), input, printer);
        this.teacherView = new TeacherView(controllers.getTeacherController(), controllers.getAssignmentController(),
                input, printer);
        this.extracurricularView = new ExtracurricularView(controllers.getClubController(),
                controllers.getEventController(), input, printer);
        this.financeView = new FinanceView(controllers.getPaymentConceptController(),
                controllers.getPaymentController(), controllers.getStudentController(), input, printer);
        this.resourceView = new ResourceView(controllers.getResourceController(), controllers.getLoanController(),
                input, printer);
        this.reportView = new ReportView(controllers.getReportController(), input, printer);
    }

    public void show() {
        int option;
        do {
            printMenu();
            option = input.readInt("Seleccione una opción: ", 1, EXIT_OPTION);
            switch (option) {
                case 1 -> studentView.show();
                case 2 -> academicView.show();
                case 3 -> teacherView.show();
                case 4 -> extracurricularView.show();
                case 5 -> financeView.show();
                case 6 -> resourceView.show();
                case 7 -> reportView.show();
                case EXIT_OPTION -> printer.printSuccess("Gracias por usar el ERP Little Horizons. ¡Hasta pronto!");
                default -> printer.printError("Opción no válida.");
            }
        } while (option != EXIT_OPTION);
    }

    private void printMenu() {
        String border = "═".repeat(BOX_WIDTH);
        printer.printLine("");
        printer.printLine("╔" + border + "╗");
        printer.printLine(String.format("║%-" + BOX_WIDTH + "s║", "   ERP Colegio Little Horizons"));
        printer.printLine(String.format("║%-" + BOX_WIDTH + "s║", "   Sistema de Gestión Educativa"));
        printer.printLine("╚" + border + "╝");
        printer.printLine("");
        printer.printLine("1. Gestión de Estudiantes");
        printer.printLine("2. Gestión Académica");
        printer.printLine("3. Gestión de Profesores");
        printer.printLine("4. Actividades Extracurriculares");
        printer.printLine("5. Gestión Financiera");
        printer.printLine("6. Gestión de Recursos");
        printer.printLine("7. Reportes y Estadísticas");
        printer.printLine("8. Salir");
        printer.printLine("");
    }
}
