package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.PaymentConceptController;
import com.littlehorizons.erp.controller.PaymentController;
import com.littlehorizons.erp.controller.StudentController;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.model.PaymentMethod;
import com.littlehorizons.erp.model.PaymentPeriod;
import com.littlehorizons.erp.model.PaymentResult;
import com.littlehorizons.erp.model.StudentConceptPrice;
import com.littlehorizons.erp.model.StudentGuardian;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;
import com.littlehorizons.erp.util.TextFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Module 5: payment concepts, custom prices, payments, delinquent students and invoices.
 */
public class FinanceView extends BaseView {

    private static final int MAX_OPTION = 12;

    private final PaymentConceptController conceptController;
    private final PaymentController paymentController;
    private final StudentController studentController;

    public FinanceView(PaymentConceptController conceptController, PaymentController paymentController,
                       StudentController studentController, ConsoleInput input, ConsolePrinter printer) {
        super(input, printer);
        this.conceptController = conceptController;
        this.paymentController = paymentController;
        this.studentController = studentController;
    }

    @Override
    public void show() {
        int option;
        do {
            printMenu();
            option = input.readMenuOption(MAX_OPTION);
            switch (option) {
                case 1 -> runAction(() -> listConcepts());
                case 2 -> runAction(() -> registerConcept());
                case 3 -> runAction(() -> updateConcept());
                case 4 -> runAction(() -> deleteConcept());
                case 5 -> runAction(() -> listCustomPrices());
                case 6 -> runAction(() -> registerCustomPrice());
                case 7 -> runAction(() -> deleteCustomPrice());
                case 8 -> runAction(() -> registerPayment());
                case 9 -> runAction(() -> showStudentPayments());
                case 10 -> runAction(() -> showGuardianPayments());
                case 11 -> runAction(() -> showDelinquents());
                case 12 -> runAction(() -> showInvoices());
                case CANCEL_OPTION -> printer.printInfo("Volviendo al menú principal...");
                default -> printUnknownOption();
            }
        } while (option != CANCEL_OPTION);
    }

    private void printMenu() {
        printer.printTitle("Gestión Financiera");
        printer.printSubtitle("Conceptos de pago");
        printer.printOption(1, "Listar conceptos de pago");
        printer.printOption(2, "Registrar concepto");
        printer.printOption(3, "Actualizar concepto");
        printer.printOption(4, "Eliminar concepto");
        printer.printOption(5, "Listar valores personalizados por estudiante");
        printer.printOption(6, "Registrar valor personalizado (descuento, beca)");
        printer.printOption(7, "Eliminar valor personalizado");
        printer.printSubtitle("Pagos y facturación");
        printer.printOption(8, "Registrar pago (genera factura electrónica PDF)");
        printer.printOption(9, "Historial de pagos de un estudiante");
        printer.printOption(10, "Historial de pagos de un acudiente (todos sus hijos)");
        printer.printOption(11, "Reporte de estudiantes morosos");
        printer.printOption(12, "Consultar facturas generadas");
        printBackOption();
    }

    private void listConcepts() {
        printer.printTable(new String[]{"ID", "Código", "Concepto", "Valor base", "Recurrente", "Activo"},
                new int[]{4, 12, 26, 14, 10, 6}, conceptController.listConcepts(), concept -> new String[]{
                        String.valueOf(concept.getId()), concept.getCode(), concept.getName(),
                        TextFormat.money(concept.getBaseAmount()), TextFormat.yesNo(concept.isRecurring()),
                        TextFormat.yesNo(concept.isActive())});
    }

    private void registerConcept() {
        PaymentConcept concept = new PaymentConcept(input.readText("Código (ej: SALIDA): "),
                input.readText("Nombre del concepto: "), input.readAmount("Valor base"),
                input.readConfirmation("¿Es un pago recurrente (mensual)?"));
        concept.setDescription(input.readOptionalText("Descripción"));
        int conceptId = conceptController.registerConcept(concept);
        printer.printSuccess("Concepto registrado con ID " + conceptId + ".");
    }

    private void updateConcept() {
        PaymentConcept concept = conceptController.findConcept(readId("concepto"));
        printer.printInfo("Presione Enter para conservar el valor actual.");
        concept.setName(input.readTextOrKeep("Nombre", concept.getName()));
        concept.setDescription(input.readTextOrKeep("Descripción", concept.getDescription()));
        concept.setBaseAmount(input.readAmountOrDefault("Valor base", concept.getBaseAmount()));
        concept.setRecurring(input.readConfirmation("¿Es recurrente?"));
        concept.setActive(input.readConfirmation("¿Está activo?"));
        conceptController.updateConcept(concept);
        printer.printSuccess("Concepto actualizado correctamente.");
    }

    private void deleteConcept() {
        PaymentConcept concept = conceptController.findConcept(readId("concepto"));
        if (input.readConfirmation("¿Seguro que desea eliminar el concepto " + concept.getName() + "?")) {
            conceptController.deleteConcept(concept.getId());
            printer.printSuccess("Concepto eliminado.");
        } else {
            printer.printInfo("Eliminación cancelada.");
        }
    }

    private void listCustomPrices() {
        printer.printTable(new String[]{"ID", "Año", "Estudiante", "Concepto", "Valor", "Motivo"},
                new int[]{4, 4, 22, 18, 12, 34}, conceptController.listCustomPrices(), price -> new String[]{
                        String.valueOf(price.getId()), String.valueOf(price.getAcademicYear()), price.getStudentName(),
                        price.getConceptName(), TextFormat.money(price.getCustomAmount()), price.getReason()});
    }

    private void registerCustomPrice() {
        int studentId = readId("estudiante");
        PaymentConcept concept = chooseConcept();
        int year = readYear();
        printer.printInfo("Valor base del concepto: " + TextFormat.money(concept.getBaseAmount()));
        StudentConceptPrice price = new StudentConceptPrice(studentId, concept.getId(), year,
                input.readAmount("Valor personalizado"), input.readText("Motivo (ej: Descuento por hermanos): "));
        int priceId = conceptController.registerCustomPrice(price);
        printer.printSuccess("Valor personalizado registrado con ID " + priceId + ".");
    }

    private void deleteCustomPrice() {
        int priceId = readId("valor personalizado");
        if (input.readConfirmation("¿Seguro que desea eliminar el valor personalizado " + priceId + "?")) {
            conceptController.deleteCustomPrice(priceId);
            printer.printSuccess("Valor personalizado eliminado.");
        } else {
            printer.printInfo("Eliminación cancelada.");
        }
    }

    private void registerPayment() {
        printer.printSubtitle("Registrar pago");
        int studentId = readId("estudiante");
        List<StudentGuardian> guardians = studentController.getGuardians(studentId);
        StudentGuardian payer = input.readChoice("Acudiente que paga:", guardians,
                link -> link.getGuardian().getFullName() + " (" + link.getGuardian().getRelationship() + ")");
        PaymentConcept concept = chooseConcept();
        LocalDate paymentDate = input.readDateOrDefault("Fecha de pago", () -> LocalDate.now());
        BigDecimal suggested = conceptController.getSuggestedAmount(studentId, concept.getId(), paymentDate.getYear());
        printer.printInfo("Valor sugerido para este estudiante: " + TextFormat.money(suggested));
        BigDecimal amount = input.readAmountOrDefault("Monto pagado", suggested);
        PaymentMethod method = readOption("Método de pago:", PaymentMethod.values(), option -> option.getLabel());
        Payment payment = new Payment(studentId, payer.getGuardian().getId(), concept.getId(), amount, paymentDate,
                method);
        payment.setPeriod(readOption("Período al que aplica:", PaymentPeriod.values(), option -> option.getLabel()));
        payment.setNotes(input.readOptionalText("Notas"));

        if (!input.readConfirmation("¿Confirmar el pago de " + TextFormat.money(amount) + " por "
                + concept.getName() + "?")) {
            printer.printInfo("Pago cancelado. No se guardó ningún dato.");
            return;
        }
        PaymentResult result = paymentController.registerPayment(payment);
        printPaymentResult(result);
    }

    private void printPaymentResult(PaymentResult result) {
        Invoice invoice = result.getInvoice();
        printer.printSuccess("Pago registrado. Recibo No. " + result.getPayment().getReceiptNumber() + ".");
        printer.printDetail("Factura electrónica", invoice.getInvoiceNumber());
        printer.printDetail("Subtotal", TextFormat.money(invoice.getSubtotal()));
        printer.printDetail(invoice.getTaxDescription(), TextFormat.money(invoice.getTaxAmount()));
        printer.printDetail("Total", TextFormat.money(invoice.getTotal()));
        printer.printDetail("CUFE", invoice.getCufe().substring(0, 32) + "...");
        if (result.isPdfGenerated()) {
            printer.printSuccess("Factura PDF guardada en: " + invoice.getFilePath());
        } else {
            printer.printWarning("El pago quedó registrado, pero no se pudo generar el PDF. "
                    + "Revise error_log.txt.");
        }
    }

    private void showStudentPayments() {
        printPayments(paymentController.getPaymentsByStudent(readId("estudiante")));
    }

    private void showGuardianPayments() {
        printPayments(paymentController.getPaymentsByGuardian(readId("acudiente")));
    }

    private void printPayments(List<Payment> payments) {
        printer.printTable(new String[]{"Recibo", "Fecha", "Estudiante", "Concepto", "Monto", "Método"},
                new int[]{9, 10, 22, 22, 12, 15}, payments, payment -> new String[]{
                        TextFormat.orDash(payment.getReceiptNumber()), TextFormat.date(payment.getPaymentDate()),
                        payment.getStudentName(), payment.getConceptName(), TextFormat.money(payment.getAmount()),
                        payment.getMethod().getLabel()});
        printer.printInfo("Total pagado: " + TextFormat.money(paymentController.sumAmounts(payments)));
    }

    private void showDelinquents() {
        printer.printSubtitle("Morosos: concepto recurrente sin pagar hace más de un mes (al " + LocalDate.now() + ")");
        printer.printTable(new String[]{"ID", "Estudiante", "Concepto", "Último pago", "Días"},
                new int[]{4, 30, 24, 12, 5}, paymentController.findDelinquentStudents(LocalDate.now()),
                record -> new String[]{String.valueOf(record.getStudentId()), record.getStudentName(),
                        record.getConceptName(), record.getLastPaymentDate() == null ? "Sin pagos"
                        : record.getLastPaymentDate().toString(), String.valueOf(record.getDaysWithoutPayment())});
    }

    private void showInvoices() {
        List<Invoice> invoices = paymentController.listInvoices();
        printer.printTable(new String[]{"Factura", "Emisión", "Recibo", "Estudiante", "Total", "PDF existe"},
                new int[]{9, 10, 9, 22, 12, 10}, invoices, invoice -> new String[]{invoice.getInvoiceNumber(),
                        TextFormat.date(invoice.getIssueDate()), invoice.getReceiptNumber(), invoice.getStudentName(),
                        TextFormat.money(invoice.getPaidAmount()),
                        TextFormat.yesNo(paymentController.invoiceFileExists(invoice))});
        for (Invoice invoice : invoices) {
            printer.printLine("  " + invoice.getInvoiceNumber() + " -> " + invoice.getFilePath());
        }
    }

    private PaymentConcept chooseConcept() {
        return input.readChoice("Concepto:", conceptController.listActiveConcepts(),
                concept -> concept.getName() + " (" + TextFormat.money(concept.getBaseAmount()) + ")");
    }
}
