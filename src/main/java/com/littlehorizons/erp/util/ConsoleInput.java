package com.littlehorizons.erp.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Reads and validates user input. Every invalid value shows a message and asks again.
 * It owns the only Scanner of the application.
 */
public class ConsoleInput {

    private static final double MIN_GRADE = 0.0;
    private static final double MAX_GRADE = 5.0;
    private static final String YES = "S";
    private static final String NO = "N";

    private final Scanner scanner;
    private final ConsolePrinter printer;

    public ConsoleInput(ConsolePrinter printer) {
        this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        this.printer = printer;
    }

    /**
     * Generic validated read: converts the text and checks it until the value is valid.
     */
    public <T> T readValidated(String prompt, Function<String, T> converter, Predicate<T> validator,
                               String errorMessage) {
        while (true) {
            String text = readLine(prompt);
            try {
                T value = converter.apply(text);
                if (validator.test(value)) {
                    return value;
                }
            } catch (NumberFormatException | DateTimeParseException e) {
                // invalid format: the same error message is shown below
            }
            printer.printError(errorMessage);
        }
    }

    public int readInt(String prompt, int min, int max) {
        return readValidated(prompt, text -> Integer.parseInt(text),
                number -> number >= min && number <= max,
                "Ingrese un número entero entre " + min + " y " + max + ".");
    }

    public int readPositiveInt(String prompt) {
        return readValidated(prompt, text -> Integer.parseInt(text), number -> number > 0,
                "Ingrese un número entero mayor que cero.");
    }

    public int readMenuOption(int maxOption) {
        return readInt("Seleccione una opción: ", 0, maxOption);
    }

    public String readText(String prompt) {
        return readValidated(prompt, text -> text, text -> !text.isEmpty(),
                "Este campo es obligatorio.");
    }

    public String readOptionalText(String prompt) {
        String text = readLine(prompt + " (opcional, Enter para omitir): ");
        return text.isEmpty() ? null : text;
    }

    public String readTextOrKeep(String prompt, String currentValue) {
        String text = readLine(prompt + " [" + TextFormat.orDash(currentValue) + "]: ");
        return text.isEmpty() ? currentValue : text;
    }

    public String readOptionalEmail(String prompt) {
        return readValidated(prompt + " (opcional, Enter para omitir): ", text -> text.isEmpty() ? null : text,
                email -> email == null || isValidEmail(email),
                "Correo inválido. Ejemplo: nombre@dominio.com");
    }

    public String readEmail(String prompt) {
        return readValidated(prompt, text -> text, email -> isValidEmail(email),
                "Correo inválido. Ejemplo: nombre@dominio.com");
    }

    public LocalDate readDate(String prompt) {
        return readValidated(prompt + " (yyyy-MM-dd): ", text -> LocalDate.parse(text),
                date -> date.getYear() >= 1900, "Fecha inválida. Use el formato yyyy-MM-dd, por ejemplo 2026-03-15.");
    }

    public LocalDate readDateOrDefault(String prompt, Supplier<LocalDate> defaultDate) {
        LocalDate suggested = defaultDate.get();
        return readValidated(prompt + " (yyyy-MM-dd) [" + suggested + "]: ",
                text -> text.isEmpty() ? suggested : LocalDate.parse(text),
                date -> date.getYear() >= 1900, "Fecha inválida. Use el formato yyyy-MM-dd, por ejemplo 2026-03-15.");
    }

    public LocalTime readTime(String prompt) {
        return readValidated(prompt + " (HH:mm): ", text -> LocalTime.parse(text),
                time -> true, "Hora inválida. Use el formato HH:mm, por ejemplo 07:30.");
    }

    public double readGrade(String prompt) {
        return readValidated(prompt + " (0.0 a 5.0): ", text -> new BigDecimal(text.replace(',', '.')),
                grade -> grade.scale() <= 1 && grade.doubleValue() >= MIN_GRADE && grade.doubleValue() <= MAX_GRADE,
                "La nota debe estar entre 0.0 y 5.0 con máximo un decimal.").doubleValue();
    }

    public BigDecimal readAmount(String prompt) {
        return readValidated(prompt + ": ", text -> new BigDecimal(text),
                amount -> amount.signum() > 0 && amount.scale() <= 2,
                "Ingrese un monto mayor que cero, sin separadores de miles (ej: 350000).");
    }

    public BigDecimal readAmountOrDefault(String prompt, BigDecimal suggestedAmount) {
        return readValidated(prompt + " [" + suggestedAmount.toPlainString() + "]: ",
                text -> text.isEmpty() ? suggestedAmount : new BigDecimal(text),
                amount -> amount.signum() > 0 && amount.scale() <= 2,
                "Ingrese un monto mayor que cero, sin separadores de miles (ej: 350000).");
    }

    public boolean readConfirmation(String prompt) {
        String answer = readValidated(prompt + " (S/N): ", text -> text,
                text -> text.equalsIgnoreCase(YES) || text.equalsIgnoreCase(NO),
                "Responda S para sí o N para no.");
        return answer.equalsIgnoreCase(YES);
    }

    /**
     * Shows a numbered list and returns the chosen element.
     */
    public <T> T readChoice(String title, List<T> options, Function<T, String> labelOf) {
        printer.printLine(title);
        for (int index = 0; index < options.size(); index++) {
            printer.printOption(index + 1, labelOf.apply(options.get(index)));
        }
        int chosen = readInt("Opción: ", 1, options.size());
        return options.get(chosen - 1);
    }

    public void waitForEnter() {
        readLine("Presione Enter para continuar...");
    }

    private boolean isValidEmail(String email) {
        int atPosition = email.indexOf('@');
        return atPosition > 0 && email.indexOf('.', atPosition) > atPosition + 1 && !email.contains(" ");
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
