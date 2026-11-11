package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Person;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.BusinessRuleException;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;
import com.littlehorizons.erp.util.DataAccessException;
import com.littlehorizons.erp.util.TextFormat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Common behavior of the module views: run an action safely and read common data.
 * Views only print and read; every rule lives in the controllers.
 */
public abstract class BaseView {

    protected static final int CANCEL_OPTION = 0;
    private static final int MIN_YEAR = 2000;
    private static final int MAX_YEAR = 2100;

    protected final ConsoleInput input;
    protected final ConsolePrinter printer;

    protected BaseView(ConsoleInput input, ConsolePrinter printer) {
        this.input = input;
        this.printer = printer;
    }

    /** Shows the module menu until the user chooses 0. */
    public abstract void show();

    /**
     * Runs one menu action and turns any expected error into a clear message.
     * The DAO already logged database errors with their stack trace.
     */
    protected void runAction(Runnable action) {
        try {
            action.run();
        } catch (OperationCancelledException e) {
            AppLogger.getInstance().info(getClass().getSimpleName(), e.getMessage());
            printer.printInfo("Operación cancelada. Volviendo al menú.");
        } catch (BusinessRuleException e) {
            AppLogger.getInstance().warning(getClass().getSimpleName(), e.getMessage());
            printer.printError(e.getMessage());
        } catch (IllegalArgumentException e) {
            AppLogger.getInstance().warning(getClass().getSimpleName(), "Invalid data: " + e.getMessage());
            printer.printError(e.getMessage());
        } catch (DataAccessException e) {
            AppLogger.getInstance().warning(getClass().getSimpleName(), "Shown to user: " + e.getMessage());
            printer.printError(e.getMessage());
        }
        input.waitForEnter();
    }

    protected void printBackOption() {
        printer.printOption(CANCEL_OPTION, "Volver al menú principal");
    }

    protected void printUnknownOption() {
        printer.printError("Opción no válida.");
    }

    /** Reads an id; 0 cancels the operation. */
    protected int readId(String entityName) {
        int id = input.readValidated("ID del " + entityName + " (0 para cancelar): ", text -> Integer.parseInt(text),
                number -> number >= 0, "Ingrese un ID numérico, o 0 para cancelar.");
        if (id == CANCEL_OPTION) {
            throw new OperationCancelledException();
        }
        return id;
    }

    protected int readYear() {
        return input.readIntOrDefault("Año académico", MIN_YEAR, MAX_YEAR, LocalDate.now().getYear());
    }

    protected <T> T readOption(String title, T[] values, Function<T, String> labelOf) {
        List<T> options = Arrays.asList(values);
        return input.readChoice(title, options, labelOf);
    }

    /** Asks again every common field of a person, keeping the current value on Enter. */
    protected void updatePersonData(Person person) {
        person.setDocumentNumber(input.readTextOrKeep("Número de documento", person.getDocumentNumber()));
        person.setFirstName(input.readTextOrKeep("Primer nombre", person.getFirstName()));
        person.setMiddleName(input.readTextOrKeep("Segundo nombre", person.getMiddleName()));
        person.setLastName(input.readTextOrKeep("Primer apellido", person.getLastName()));
        person.setSecondLastName(input.readTextOrKeep("Segundo apellido", person.getSecondLastName()));
        person.setPhone(input.readTextOrKeep("Teléfono", person.getPhone()));
        person.setEmail(input.readTextOrKeep("Correo electrónico", person.getEmail()));
        person.setAddress(input.readTextOrKeep("Dirección", person.getAddress()));
    }

    protected void printPersonDetail(Person person) {
        printer.printDetail("ID", String.valueOf(person.getId()));
        printer.printDetail("Rol", person.getRoleName());
        printer.printDetail("Documento", person.getDocumentType().getDbValue() + " " + person.getDocumentNumber());
        printer.printDetail("Nombre completo", person.getFullName());
        printer.printDetail("Teléfono", TextFormat.orDash(person.getPhone()));
        printer.printDetail("Correo", TextFormat.orDash(person.getEmail()));
        printer.printDetail("Dirección", TextFormat.orDash(person.getAddress()));
    }
}
