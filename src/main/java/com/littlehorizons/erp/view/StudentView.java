package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.EnrollmentController;
import com.littlehorizons.erp.controller.GuardianController;
import com.littlehorizons.erp.controller.StudentController;
import com.littlehorizons.erp.model.AcademicPeriod;
import com.littlehorizons.erp.model.DocumentType;
import com.littlehorizons.erp.model.Enrollment;
import com.littlehorizons.erp.model.EnrollmentStatus;
import com.littlehorizons.erp.model.Gender;
import com.littlehorizons.erp.model.Grade;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentStatus;
import com.littlehorizons.erp.model.Subject;
import com.littlehorizons.erp.model.SubjectRegistration;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;
import com.littlehorizons.erp.util.TextFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * Module 1: students, guardians, enrollment by grade and registration in subjects.
 */
public class StudentView extends BaseView {

    private static final int MAX_OPTION = 16;
    private static final String[] STUDENT_HEADERS = {"ID", "Documento", "Nombre completo", "Nacimiento", "Estado"};
    private static final int[] STUDENT_WIDTHS = {4, 14, 34, 10, 10};
    private static final String[] GUARDIAN_HEADERS = {"ID", "Documento", "Nombre completo", "Parentesco", "Teléfono"};
    private static final int[] GUARDIAN_WIDTHS = {4, 14, 30, 12, 12};

    private final StudentController studentController;
    private final GuardianController guardianController;
    private final EnrollmentController enrollmentController;

    public StudentView(StudentController studentController, GuardianController guardianController,
                       EnrollmentController enrollmentController, ConsoleInput input, ConsolePrinter printer) {
        super(input, printer);
        this.studentController = studentController;
        this.guardianController = guardianController;
        this.enrollmentController = enrollmentController;
    }

    @Override
    public void show() {
        int option;
        do {
            printMenu();
            option = input.readMenuOption(MAX_OPTION);
            switch (option) {
                case 1 -> runAction(() -> listStudents(studentController.listStudents()));
                case 2 -> runAction(() -> searchStudents());
                case 3 -> runAction(() -> showStudentDetail());
                case 4 -> runAction(() -> registerStudent());
                case 5 -> runAction(() -> updateStudent());
                case 6 -> runAction(() -> deleteStudent());
                case 7 -> runAction(() -> listGuardians(guardianController.listGuardians()));
                case 8 -> runAction(() -> registerGuardian());
                case 9 -> runAction(() -> updateGuardian());
                case 10 -> runAction(() -> deleteGuardian());
                case 11 -> runAction(() -> linkGuardian());
                case 12 -> runAction(() -> showGuardianStudents());
                case 13 -> runAction(() -> enrollStudent());
                case 14 -> runAction(() -> changeEnrollmentStatus());
                case 15 -> runAction(() -> registerInSubject());
                case 16 -> runAction(() -> showRegistrations());
                case CANCEL_OPTION -> printer.printInfo("Volviendo al menú principal...");
                default -> printUnknownOption();
            }
        } while (option != CANCEL_OPTION);
    }

    private void printMenu() {
        printer.printTitle("Gestión de Estudiantes");
        printer.printSubtitle("Estudiantes");
        printer.printOption(1, "Listar estudiantes");
        printer.printOption(2, "Buscar estudiante por nombre");
        printer.printOption(3, "Ver ficha completa de un estudiante");
        printer.printOption(4, "Registrar estudiante");
        printer.printOption(5, "Actualizar estudiante");
        printer.printOption(6, "Eliminar estudiante");
        printer.printSubtitle("Acudientes");
        printer.printOption(7, "Listar acudientes");
        printer.printOption(8, "Registrar acudiente");
        printer.printOption(9, "Actualizar acudiente");
        printer.printOption(10, "Eliminar acudiente");
        printer.printOption(11, "Vincular acudiente a un estudiante");
        printer.printOption(12, "Ver estudiantes de un acudiente (hermanos)");
        printer.printSubtitle("Matrícula e inscripción en materias");
        printer.printOption(13, "Matricular estudiante en un grado");
        printer.printOption(14, "Cambiar estado de una matrícula");
        printer.printOption(15, "Inscribir estudiante en una materia");
        printer.printOption(16, "Ver inscripciones de un estudiante");
        printBackOption();
    }

    private void listStudents(List<Student> students) {
        printer.printSubtitle("Estudiantes");
        printer.printTable(STUDENT_HEADERS, STUDENT_WIDTHS, students, student -> new String[]{
                String.valueOf(student.getId()), student.getDocumentType().getDbValue() + " " + student.getDocumentNumber(),
                student.getFullName(), TextFormat.date(student.getBirthDate()), student.getStatus().getLabel()});
    }

    private void searchStudents() {
        String text = input.readText("Texto a buscar en el nombre: ");
        listStudents(studentController.searchByName(text));
    }

    private void showStudentDetail() {
        Student student = studentController.findStudent(readId("estudiante"));
        printer.printSubtitle("Ficha del estudiante");
        printPersonDetail(student);
        printer.printDetail("Fecha de nacimiento", student.getBirthDate() + " (" + student.getAgeOn(LocalDate.now())
                + " años)");
        printer.printDetail("Género", student.getGender().getLabel());
        printer.printDetail("Tipo de sangre", TextFormat.orDash(student.getBloodType()));
        printer.printDetail("Alergias", TextFormat.orDash(student.getAllergies()));
        printer.printDetail("Condiciones médicas", TextFormat.orDash(student.getMedicalConditions()));
        printer.printDetail("Fecha de ingreso", TextFormat.date(student.getAdmissionDate()));
        printer.printDetail("Estado", student.getStatus().getLabel());

        printer.printSubtitle("Acudientes y contactos de emergencia");
        printer.printTable(new String[]{"ID", "Nombre", "Parentesco", "Teléfono", "Principal", "Emergencia"},
                new int[]{4, 28, 12, 12, 9, 10}, studentController.getGuardians(student.getId()),
                link -> new String[]{String.valueOf(link.getGuardian().getId()), link.getGuardian().getFullName(),
                        link.getGuardian().getRelationship(), link.getGuardian().getPhone(),
                        TextFormat.yesNo(link.isPrimaryGuardian()), TextFormat.yesNo(link.isEmergencyContact())});

        printer.printSubtitle("Matrículas");
        printEnrollments(enrollmentController.getEnrollments(student.getId()));
    }

    private void registerStudent() {
        printer.printSubtitle("Registrar estudiante");
        printer.printInfo("Todo estudiante necesita al menos un acudiente registrado previamente.");
        int guardianId = readId("acudiente responsable");
        Guardian guardian = guardianController.findGuardian(guardianId);
        printer.printInfo("Acudiente: " + guardian.getFullName());

        DocumentType documentType = readOption("Tipo de documento:", DocumentType.values(), type -> type.getLabel());
        Student student = new Student.Builder(documentType, input.readText("Número de documento: "),
                input.readText("Primer nombre: "), input.readText("Primer apellido: "))
                .middleName(input.readOptionalText("Segundo nombre"))
                .secondLastName(input.readOptionalText("Segundo apellido"))
                .birthDate(input.readDate("Fecha de nacimiento"))
                .gender(readOption("Género:", Gender.values(), gender -> gender.getLabel()))
                .address(input.readOptionalText("Dirección"))
                .phone(input.readOptionalText("Teléfono"))
                .email(input.readOptionalEmail("Correo electrónico"))
                .bloodType(input.readOptionalText("Tipo de sangre (ej: O+)"))
                .allergies(input.readOptionalText("Alergias"))
                .medicalConditions(input.readOptionalText("Condiciones médicas"))
                .admissionDate(input.readDateOrDefault("Fecha de ingreso", () -> LocalDate.now()))
                .build();
        boolean emergencyContact = input.readConfirmation("¿El acudiente es contacto de emergencia?");
        int studentId = studentController.registerStudent(student, guardianId, emergencyContact);
        printer.printSuccess("Estudiante registrado con ID " + studentId + " y vinculado a " + guardian.getFullName()
                + ".");
    }

    private void updateStudent() {
        Student student = studentController.findStudent(readId("estudiante"));
        printer.printInfo("Presione Enter para conservar el valor actual.");
        updatePersonData(student);
        if (input.readConfirmation("¿Cambiar el estado (actual: " + student.getStatus().getLabel() + ")?")) {
            student.setStatus(readOption("Nuevo estado:", StudentStatus.values(), status -> status.getLabel()));
        }
        studentController.updateStudent(student);
        printer.printSuccess("Estudiante actualizado correctamente.");
    }

    private void deleteStudent() {
        Student student = studentController.findStudent(readId("estudiante"));
        if (input.readConfirmation("¿Seguro que desea eliminar a " + student.getFullName() + "?")) {
            studentController.deleteStudent(student.getId());
            printer.printSuccess("Estudiante eliminado.");
        } else {
            printer.printInfo("Eliminación cancelada.");
        }
    }

    private void listGuardians(List<Guardian> guardians) {
        printer.printSubtitle("Acudientes");
        printer.printTable(GUARDIAN_HEADERS, GUARDIAN_WIDTHS, guardians, guardian -> new String[]{
                String.valueOf(guardian.getId()), guardian.getDocumentType().getDbValue() + " "
                + guardian.getDocumentNumber(), guardian.getFullName(), guardian.getRelationship(), guardian.getPhone()});
    }

    private void registerGuardian() {
        printer.printSubtitle("Registrar acudiente");
        DocumentType documentType = readOption("Tipo de documento:",
                new DocumentType[]{DocumentType.CC, DocumentType.CE, DocumentType.PASSPORT}, type -> type.getLabel());
        Guardian guardian = new Guardian(documentType, input.readText("Número de documento: "),
                input.readText("Primer nombre: "), input.readText("Primer apellido: "),
                input.readText("Parentesco (Padre, Madre, Abuelo, Tutor Legal...): "),
                input.readText("Teléfono principal: "));
        guardian.setMiddleName(input.readOptionalText("Segundo nombre"));
        guardian.setSecondLastName(input.readOptionalText("Segundo apellido"));
        guardian.setSecondaryPhone(input.readOptionalText("Teléfono secundario"));
        guardian.setEmail(input.readOptionalEmail("Correo electrónico"));
        guardian.setAddress(input.readOptionalText("Dirección"));
        guardian.setOccupation(input.readOptionalText("Ocupación"));
        guardian.setFinancialResponsible(input.readConfirmation("¿Es responsable financiero?"));
        int guardianId = guardianController.registerGuardian(guardian);
        printer.printSuccess("Acudiente registrado con ID " + guardianId + ".");
    }

    private void updateGuardian() {
        Guardian guardian = guardianController.findGuardian(readId("acudiente"));
        printer.printInfo("Presione Enter para conservar el valor actual.");
        updatePersonData(guardian);
        guardian.setRelationship(input.readTextOrKeep("Parentesco", guardian.getRelationship()));
        guardian.setSecondaryPhone(input.readTextOrKeep("Teléfono secundario", guardian.getSecondaryPhone()));
        guardian.setOccupation(input.readTextOrKeep("Ocupación", guardian.getOccupation()));
        guardianController.updateGuardian(guardian);
        printer.printSuccess("Acudiente actualizado correctamente.");
    }

    private void deleteGuardian() {
        Guardian guardian = guardianController.findGuardian(readId("acudiente"));
        if (input.readConfirmation("¿Seguro que desea eliminar a " + guardian.getFullName() + "?")) {
            guardianController.deleteGuardian(guardian.getId());
            printer.printSuccess("Acudiente eliminado.");
        } else {
            printer.printInfo("Eliminación cancelada.");
        }
    }

    private void linkGuardian() {
        int studentId = readId("estudiante");
        int guardianId = readId("acudiente");
        boolean emergencyContact = input.readConfirmation("¿Es contacto de emergencia?");
        studentController.linkGuardian(studentId, guardianId, emergencyContact);
        printer.printSuccess("Acudiente vinculado al estudiante.");
    }

    private void showGuardianStudents() {
        Guardian guardian = guardianController.findGuardian(readId("acudiente"));
        printer.printInfo("Estudiantes a cargo de " + guardian.getFullName() + ":");
        listStudents(guardianController.getStudents(guardian.getId()));
    }

    private void enrollStudent() {
        int studentId = readId("estudiante");
        Grade grade = input.readChoice("Grado:", enrollmentController.listGrades(), option -> option.getName());
        int year = readYear();
        LocalDate date = input.readDateOrDefault("Fecha de matrícula", () -> LocalDate.now());
        int enrollmentId = enrollmentController.enrollStudent(studentId, grade.getId(), year, date);
        printer.printSuccess("Matrícula registrada con ID " + enrollmentId + " en " + grade.getName() + " " + year + ".");
    }

    private void changeEnrollmentStatus() {
        int studentId = readId("estudiante");
        printEnrollments(enrollmentController.getEnrollments(studentId));
        int year = readYear();
        EnrollmentStatus status = readOption("Nuevo estado:", EnrollmentStatus.values(), option -> option.getLabel());
        LocalDate withdrawalDate = null;
        if (status == EnrollmentStatus.WITHDRAWN) {
            withdrawalDate = input.readDateOrDefault("Fecha de retiro", () -> LocalDate.now());
        }
        enrollmentController.changeEnrollmentStatus(studentId, year, status, withdrawalDate);
        printer.printSuccess("Estado de la matrícula " + year + " cambiado a " + status.getLabel() + ".");
    }

    private void registerInSubject() {
        int studentId = readId("estudiante");
        int year = readYear();
        List<Subject> subjects = enrollmentController.getSubjectsForStudent(studentId, year);
        Subject subject = input.readChoice("Materias del grado actual:", subjects,
                option -> option.getCode() + " - " + option.getName());
        AcademicPeriod period = readOption("Período:", new AcademicPeriod[]{AcademicPeriod.FIRST,
                AcademicPeriod.SECOND, AcademicPeriod.THIRD, AcademicPeriod.FOURTH}, option -> option.getLabel());
        int registrationId = enrollmentController.registerInSubject(studentId, subject.getId(), year, period);
        printer.printSuccess("Inscripción registrada con ID " + registrationId + ".");
    }

    private void showRegistrations() {
        List<SubjectRegistration> registrations = enrollmentController.getRegistrations(readId("estudiante"));
        printer.printTable(new String[]{"Año", "Período", "Materia", "Sección", "Estado"},
                new int[]{4, 7, 24, 7, 10}, registrations, registration -> new String[]{
                        String.valueOf(registration.getAcademicYear()), registration.getPeriod().getDbValue(),
                        registration.getSubjectName(), TextFormat.orDash(registration.getSection()),
                        registration.getStatus().getLabel()});
    }

    private void printEnrollments(List<Enrollment> enrollments) {
        printer.printTable(new String[]{"ID", "Año", "Grado", "Estado", "Fecha", "Retiro"},
                new int[]{4, 4, 11, 10, 10, 10}, enrollments, enrollment -> new String[]{
                        String.valueOf(enrollment.getId()), String.valueOf(enrollment.getAcademicYear()),
                        enrollment.getGradeName(), enrollment.getStatus().getLabel(),
                        TextFormat.date(enrollment.getEnrollmentDate()), TextFormat.date(enrollment.getWithdrawalDate())});
    }
}
