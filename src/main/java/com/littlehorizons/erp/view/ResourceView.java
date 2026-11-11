package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.LoanController;
import com.littlehorizons.erp.controller.ResourceController;
import com.littlehorizons.erp.model.Loan;
import com.littlehorizons.erp.model.Resource;
import com.littlehorizons.erp.model.ResourceCategory;
import com.littlehorizons.erp.model.ResourceStatus;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;
import com.littlehorizons.erp.util.TextFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * Module 6: resource inventory, loans, returns and overdue loans.
 */
public class ResourceView extends BaseView {

    private static final int MAX_OPTION = 10;
    private static final int DEFAULT_LOAN_DAYS = 14;
    private static final String STUDENT_BORROWER = "Estudiante";
    private static final String TEACHER_BORROWER = "Profesor";

    private final ResourceController resourceController;
    private final LoanController loanController;

    public ResourceView(ResourceController resourceController, LoanController loanController, ConsoleInput input,
                        ConsolePrinter printer) {
        super(input, printer);
        this.resourceController = resourceController;
        this.loanController = loanController;
    }

    @Override
    public void show() {
        int option;
        do {
            printMenu();
            option = input.readMenuOption(MAX_OPTION);
            switch (option) {
                case 1 -> runAction(() -> listResources(resourceController.listResources()));
                case 2 -> runAction(() -> registerResource());
                case 3 -> runAction(() -> updateResource());
                case 4 -> runAction(() -> deleteResource());
                case 5 -> runAction(() -> lendToStudent());
                case 6 -> runAction(() -> lendToTeacher());
                case 7 -> runAction(() -> registerReturn());
                case 8 -> runAction(() -> printLoans(loanController.listOpenLoans()));
                case 9 -> runAction(() -> showOverdueLoans());
                case 10 -> runAction(() -> showLoanHistory());
                case CANCEL_OPTION -> printer.printInfo("Volviendo al menú principal...");
                default -> printUnknownOption();
            }
        } while (option != CANCEL_OPTION);
    }

    private void printMenu() {
        printer.printTitle("Gestión de Recursos");
        printer.printSubtitle("Inventario");
        printer.printOption(1, "Listar recursos");
        printer.printOption(2, "Registrar recurso");
        printer.printOption(3, "Actualizar recurso");
        printer.printOption(4, "Eliminar recurso");
        printer.printSubtitle("Préstamos");
        printer.printOption(5, "Prestar recurso a un estudiante");
        printer.printOption(6, "Prestar recurso a un profesor");
        printer.printOption(7, "Registrar devolución");
        printer.printOption(8, "Ver préstamos sin devolver (activos y vencidos)");
        printer.printOption(9, "Reporte de préstamos vencidos");
        printer.printOption(10, "Historial completo de préstamos");
        printBackOption();
    }

    private void listResources(List<Resource> resources) {
        printer.printTable(new String[]{"ID", "Código", "Nombre", "Categoría", "Estado", "Ubicación"},
                new int[]{4, 8, 30, 16, 16, 22}, resources, resource -> new String[]{String.valueOf(resource.getId()),
                        resource.getCode(), resource.getName(), resource.getCategoryName(),
                        resource.getStatus().getLabel(), TextFormat.orDash(resource.getLocation())});
    }

    private void registerResource() {
        ResourceCategory category = input.readChoice("Categoría:", resourceController.listCategories(),
                option -> option.getName());
        Resource resource = new Resource(input.readText("Código único (ej: LIB-050): "),
                input.readText("Nombre del recurso: "), category.getId(),
                input.readOptionalText("Ubicación física"));
        resource.setDescription(input.readOptionalText("Descripción"));
        resource.setAcquisitionDate(input.readDateOrDefault("Fecha de adquisición", () -> LocalDate.now()));
        int resourceId = resourceController.registerResource(resource);
        printer.printSuccess("Recurso registrado con ID " + resourceId + " en estado Disponible.");
    }

    private void updateResource() {
        Resource resource = resourceController.findResource(readId("recurso"));
        printer.printInfo("Presione Enter para conservar el valor actual.");
        resource.setCode(input.readTextOrKeep("Código", resource.getCode()));
        resource.setName(input.readTextOrKeep("Nombre", resource.getName()));
        resource.setLocation(input.readTextOrKeep("Ubicación", resource.getLocation()));
        resource.setDescription(input.readTextOrKeep("Descripción", resource.getDescription()));
        if (input.readConfirmation("¿Cambiar el estado (actual: " + resource.getStatus().getLabel() + ")?")) {
            resource.setStatus(readOption("Nuevo estado:", new ResourceStatus[]{ResourceStatus.AVAILABLE,
                    ResourceStatus.MAINTENANCE, ResourceStatus.RETIRED}, status -> status.getLabel()));
        }
        resourceController.updateResource(resource);
        printer.printSuccess("Recurso actualizado correctamente.");
    }

    private void deleteResource() {
        Resource resource = resourceController.findResource(readId("recurso"));
        if (input.readConfirmation("¿Seguro que desea eliminar " + resource.getCode() + " - " + resource.getName()
                + " y su historial de préstamos?")) {
            resourceController.deleteResource(resource.getId());
            printer.printSuccess("Recurso eliminado.");
        } else {
            printer.printInfo("Eliminación cancelada.");
        }
    }

    private void lendToStudent() {
        listResources(resourceController.listAvailableResources());
        int resourceId = readId("recurso");
        int studentId = readId("estudiante");
        LocalDate loanDate = input.readDateOrDefault("Fecha de préstamo", () -> LocalDate.now());
        LocalDate dueDate = input.readDateOrDefault("Fecha de devolución programada",
                () -> loanDate.plusDays(DEFAULT_LOAN_DAYS));
        int loanId = loanController.lendToStudent(new StudentLoan(resourceId, studentId, loanDate, dueDate));
        printer.printSuccess("Préstamo a estudiante registrado con ID " + loanId + ". El recurso quedó Prestado.");
    }

    private void lendToTeacher() {
        listResources(resourceController.listAvailableResources());
        int resourceId = readId("recurso");
        int teacherId = readId("profesor");
        LocalDate loanDate = input.readDateOrDefault("Fecha de préstamo", () -> LocalDate.now());
        LocalDate dueDate = input.readDateOrDefault("Fecha de devolución programada",
                () -> loanDate.plusDays(DEFAULT_LOAN_DAYS));
        int loanId = loanController.lendToTeacher(new TeacherLoan(resourceId, teacherId, loanDate, dueDate));
        printer.printSuccess("Préstamo a profesor registrado con ID " + loanId + ". El recurso quedó Prestado.");
    }

    private void registerReturn() {
        printLoans(loanController.listOpenLoans());
        String borrowerType = input.readChoice("Tipo de préstamo:", List.of(STUDENT_BORROWER, TEACHER_BORROWER),
                type -> type);
        int loanId = readId("préstamo");
        LocalDate returnDate = input.readDateOrDefault("Fecha real de devolución", () -> LocalDate.now());
        String notes = input.readOptionalText("Observaciones (estado del recurso)");
        if (borrowerType.equals(STUDENT_BORROWER)) {
            loanController.returnStudentLoan(loanId, returnDate, notes);
        } else {
            loanController.returnTeacherLoan(loanId, returnDate, notes);
        }
        printer.printSuccess("Devolución registrada. El recurso volvió a estar Disponible.");
    }

    private void showOverdueLoans() {
        printer.printSubtitle("Préstamos vencidos al " + LocalDate.now());
        printLoans(loanController.findOverdueLoans());
    }

    private void showLoanHistory() {
        printer.printSubtitle("Préstamos a estudiantes");
        printLoans(List.copyOf(loanController.listStudentLoans()));
        printer.printSubtitle("Préstamos a profesores");
        printLoans(List.copyOf(loanController.listTeacherLoans()));
    }

    private void printLoans(List<Loan> loans) {
        printer.printTable(new String[]{"ID", "Tipo", "Prestatario", "Recurso", "Préstamo", "Devolver", "Devuelto",
                "Estado"}, new int[]{4, 10, 20, 26, 10, 10, 10, 8}, loans, loan -> new String[]{
                String.valueOf(loan.getId()), loan.getBorrowerType(), loan.getBorrowerName(), loan.getResourceName(),
                TextFormat.date(loan.getLoanDate()), TextFormat.date(loan.getDueDate()),
                TextFormat.date(loan.getReturnDate()), loan.getStatus().getLabel()});
    }
}
