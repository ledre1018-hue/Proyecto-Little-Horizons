package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.AssignmentController;
import com.littlehorizons.erp.controller.TeacherController;
import com.littlehorizons.erp.model.ClassSchedule;
import com.littlehorizons.erp.model.DocumentType;
import com.littlehorizons.erp.model.Subject;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.model.TeacherStatus;
import com.littlehorizons.erp.model.WeekDay;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;
import com.littlehorizons.erp.util.TextFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Module 3: teacher directory, subject assignment and class schedule.
 */
public class TeacherView extends BaseView {

    private static final int MAX_OPTION = 9;

    private final TeacherController teacherController;
    private final AssignmentController assignmentController;

    public TeacherView(TeacherController teacherController, AssignmentController assignmentController,
                       ConsoleInput input, ConsolePrinter printer) {
        super(input, printer);
        this.teacherController = teacherController;
        this.assignmentController = assignmentController;
    }

    @Override
    public void show() {
        int option;
        do {
            printMenu();
            option = input.readMenuOption(MAX_OPTION);
            switch (option) {
                case 1 -> runAction(() -> listTeachers(teacherController.listTeachers()));
                case 2 -> runAction(() -> searchBySpecialization());
                case 3 -> runAction(() -> registerTeacher());
                case 4 -> runAction(() -> updateTeacher());
                case 5 -> runAction(() -> deleteTeacher());
                case 6 -> runAction(() -> assignSubject());
                case 7 -> runAction(() -> showAssignments());
                case 8 -> runAction(() -> addSchedule());
                case 9 -> runAction(() -> showSchedule());
                case CANCEL_OPTION -> printer.printInfo("Volviendo al menú principal...");
                default -> printUnknownOption();
            }
        } while (option != CANCEL_OPTION);
    }

    private void printMenu() {
        printer.printTitle("Gestión de Profesores");
        printer.printSubtitle("Directorio");
        printer.printOption(1, "Listar profesores");
        printer.printOption(2, "Buscar profesores por especialización");
        printer.printOption(3, "Registrar profesor");
        printer.printOption(4, "Actualizar profesor");
        printer.printOption(5, "Eliminar profesor");
        printer.printSubtitle("Carga académica");
        printer.printOption(6, "Asignar materia a un profesor");
        printer.printOption(7, "Ver materias asignadas de un profesor");
        printer.printOption(8, "Registrar horario de clase");
        printer.printOption(9, "Ver horario de un profesor");
        printBackOption();
    }

    private void listTeachers(List<Teacher> teachers) {
        printer.printTable(new String[]{"ID", "Nombre completo", "Especialización", "Correo", "Estado"},
                new int[]{4, 28, 22, 32, 8}, teachers, teacher -> new String[]{String.valueOf(teacher.getId()),
                        teacher.getFullName(), TextFormat.orDash(teacher.getSpecialization()), teacher.getEmail(),
                        teacher.getStatus().getLabel()});
    }

    private void searchBySpecialization() {
        listTeachers(teacherController.searchBySpecialization(input.readText("Especialización a buscar: ")));
    }

    private void registerTeacher() {
        printer.printSubtitle("Registrar profesor");
        DocumentType documentType = readOption("Tipo de documento:",
                new DocumentType[]{DocumentType.CC, DocumentType.CE, DocumentType.PASSPORT}, type -> type.getLabel());
        Teacher teacher = new Teacher(documentType, input.readText("Número de documento: "),
                input.readText("Primer nombre: "), input.readText("Primer apellido: "),
                input.readEmail("Correo electrónico: "),
                input.readDateOrDefault("Fecha de contratación", () -> LocalDate.now()));
        teacher.setMiddleName(input.readOptionalText("Segundo nombre"));
        teacher.setSecondLastName(input.readOptionalText("Segundo apellido"));
        teacher.setPhone(input.readOptionalText("Teléfono"));
        teacher.setSpecialization(input.readOptionalText("Especialización (Matemáticas, Español...)"));
        int teacherId = teacherController.registerTeacher(teacher);
        printer.printSuccess("Profesor registrado con ID " + teacherId + ".");
    }

    private void updateTeacher() {
        Teacher teacher = teacherController.findTeacher(readId("profesor"));
        printer.printInfo("Presione Enter para conservar el valor actual.");
        updatePersonData(teacher);
        teacher.setSpecialization(input.readTextOrKeep("Especialización", teacher.getSpecialization()));
        if (input.readConfirmation("¿Cambiar el estado (actual: " + teacher.getStatus().getLabel() + ")?")) {
            teacher.setStatus(readOption("Nuevo estado:", TeacherStatus.values(), status -> status.getLabel()));
        }
        teacherController.updateTeacher(teacher);
        printer.printSuccess("Profesor actualizado correctamente.");
    }

    private void deleteTeacher() {
        Teacher teacher = teacherController.findTeacher(readId("profesor"));
        if (input.readConfirmation("¿Seguro que desea eliminar a " + teacher.getFullName() + "?")) {
            teacherController.deleteTeacher(teacher.getId());
            printer.printSuccess("Profesor eliminado.");
        } else {
            printer.printInfo("Eliminación cancelada.");
        }
    }

    private void assignSubject() {
        int teacherId = readId("profesor");
        List<Subject> subjects = assignmentController.listSubjects();
        printer.printTable(new String[]{"ID", "Código", "Materia", "Grado"}, new int[]{4, 8, 22, 11}, subjects,
                subject -> new String[]{String.valueOf(subject.getId()), subject.getCode(), subject.getName(),
                        subject.getGradeName()});
        int subjectId = readId("materia");
        int year = readYear();
        String section = input.readText("Sección (A, B, C): ");
        int assignmentId = assignmentController.assignSubject(new TeacherAssignment(teacherId, subjectId, year,
                section));
        printer.printSuccess("Materia asignada con ID de asignación " + assignmentId + ".");
    }

    private void showAssignments() {
        printAssignments(assignmentController.getAssignments(readId("profesor")));
    }

    private void addSchedule() {
        int teacherId = readId("profesor");
        List<TeacherAssignment> assignments = assignmentController.getAssignments(teacherId);
        TeacherAssignment assignment = input.readChoice("Asignación:", assignments, option -> option.getAcademicYear()
                + " - " + option.getSubjectName() + " sección " + option.getSection());
        WeekDay day = readOption("Día:", WeekDay.values(), option -> option.getLabel());
        LocalTime start = input.readTime("Hora de inicio");
        LocalTime end = input.readTime("Hora de fin");
        int scheduleId = assignmentController.addSchedule(new ClassSchedule(assignment.getId(), day, start, end));
        printer.printSuccess("Horario registrado con ID " + scheduleId + " sin choques.");
    }

    private void showSchedule() {
        int teacherId = readId("profesor");
        int year = readYear();
        printer.printTable(new String[]{"Día", "Inicio", "Fin", "Materia"}, new int[]{10, 6, 6, 36},
                assignmentController.getSchedule(teacherId, year), schedule -> new String[]{
                        schedule.getDay().getLabel(), schedule.getStartTime().toString(),
                        schedule.getEndTime().toString(), schedule.getSubjectName()});
    }

    private void printAssignments(List<TeacherAssignment> assignments) {
        printer.printTable(new String[]{"ID", "Año", "Materia", "Sección"}, new int[]{4, 4, 34, 7}, assignments,
                assignment -> new String[]{String.valueOf(assignment.getId()),
                        String.valueOf(assignment.getAcademicYear()), assignment.getSubjectName(),
                        assignment.getSection()});
    }
}
