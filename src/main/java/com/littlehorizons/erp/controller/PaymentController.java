package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.controller.tax.TaxStrategy;
import com.littlehorizons.erp.controller.tax.TaxStrategyFactory;
import com.littlehorizons.erp.dao.GuardianDao;
import com.littlehorizons.erp.dao.InvoiceDao;
import com.littlehorizons.erp.dao.PaymentConceptDao;
import com.littlehorizons.erp.dao.PaymentDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.model.DelinquentRecord;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.model.PaymentResult;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentStatus;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.BusinessRuleException;
import com.littlehorizons.erp.util.CufeGenerator;
import com.littlehorizons.erp.util.InvoiceDocumentGenerator;
import com.littlehorizons.erp.util.InvoiceGenerationException;
import com.littlehorizons.erp.util.SchoolInfo;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Business rules for payments, payment history, delinquent students and electronic invoices.
 */
public class PaymentController {

    /** Monthly tuition applies to every active student, even if it was never paid. */
    private static final String MANDATORY_CONCEPT_CODE = "PENSION";

    private final PaymentDao paymentDao;
    private final InvoiceDao invoiceDao;
    private final StudentDao studentDao;
    private final GuardianDao guardianDao;
    private final PaymentConceptDao conceptDao;
    private final TaxStrategyFactory taxStrategyFactory;
    private final InvoiceDocumentGenerator invoiceGenerator;

    public PaymentController(PaymentDao paymentDao, InvoiceDao invoiceDao, StudentDao studentDao,
                             GuardianDao guardianDao, PaymentConceptDao conceptDao,
                             TaxStrategyFactory taxStrategyFactory, InvoiceDocumentGenerator invoiceGenerator) {
        this.paymentDao = paymentDao;
        this.invoiceDao = invoiceDao;
        this.studentDao = studentDao;
        this.guardianDao = guardianDao;
        this.conceptDao = conceptDao;
        this.taxStrategyFactory = taxStrategyFactory;
        this.invoiceGenerator = invoiceGenerator;
    }

    /**
     * Validates and saves the payment with its invoice, then creates the PDF.
     * If the PDF fails, the payment is kept and the error is logged.
     */
    public PaymentResult registerPayment(Payment payment) {
        Student student = studentDao.findById(payment.getStudentId());
        if (student == null || student.getStatus() != StudentStatus.ACTIVE) {
            throw new BusinessRuleException("El estudiante no existe o no está activo.");
        }
        Guardian payer = findLinkedGuardian(payment.getStudentId(), payment.getGuardianId());
        PaymentConcept concept = conceptDao.findById(payment.getConceptId());
        if (concept == null || !concept.isActive()) {
            throw new BusinessRuleException("El concepto de pago no existe o no está activo.");
        }
        if (payment.getAcademicYear() == null) {
            payment.setAcademicYear(payment.getPaymentDate().getYear());
        }

        Invoice invoice = buildInvoice(payment, student, payer, concept);
        paymentDao.insertWithInvoice(payment, invoice, savedInvoice -> completeInvoice(savedInvoice));
        AppLogger.getInstance().info("PaymentController.registerPayment", "Payment " + payment.getReceiptNumber()
                + " saved with invoice " + invoice.getInvoiceNumber());

        boolean pdfGenerated = true;
        try {
            invoiceGenerator.generate(invoice);
        } catch (InvoiceGenerationException e) {
            AppLogger.getInstance().error("PaymentController.registerPayment", e.getMessage(), e);
            pdfGenerated = false;
        }
        return new PaymentResult(payment, invoice, pdfGenerated);
    }

    public List<Payment> getPaymentsByStudent(int studentId) {
        if (studentDao.findById(studentId) == null) {
            throw new BusinessRuleException("No existe un estudiante con ID " + studentId + ".");
        }
        return paymentDao.findByStudent(studentId);
    }

    /** Payments of all the children of a guardian. */
    public List<Payment> getPaymentsByGuardian(int guardianId) {
        if (guardianDao.findById(guardianId) == null) {
            throw new BusinessRuleException("No existe un acudiente con ID " + guardianId + ".");
        }
        return studentDao.findByGuardian(guardianId).stream()
                .flatMap(student -> paymentDao.findByStudent(student.getId()).stream())
                .sorted((first, second) -> second.getPaymentDate().compareTo(first.getPaymentDate()))
                .toList();
    }

    public List<Payment> listAllPayments() {
        return paymentDao.findAll();
    }

    public List<Invoice> listInvoices() {
        return invoiceDao.findAll();
    }

    public boolean invoiceFileExists(Invoice invoice) {
        return invoice.getFilePath() != null && new File(invoice.getFilePath()).exists();
    }

    /** Payment method label -> payments, ordered by label. */
    public Map<String, List<Payment>> groupPaymentsByMethod() {
        return paymentDao.findAll().stream()
                .collect(Collectors.groupingBy(payment -> payment.getMethod().getLabel(), () -> new TreeMap<>(),
                        Collectors.toList()));
    }

    /** Concept name -> payments, ordered by name. */
    public Map<String, List<Payment>> groupPaymentsByConcept() {
        return paymentDao.findAll().stream()
                .collect(Collectors.groupingBy(payment -> payment.getConceptName(), () -> new TreeMap<>(),
                        Collectors.toList()));
    }

    public BigDecimal sumAmounts(List<Payment> payments) {
        return payments.stream()
                .map(payment -> payment.getAmount())
                .reduce(BigDecimal.ZERO, (total, amount) -> total.add(amount));
    }

    /**
     * Delinquent rule: an active student owes a recurring concept when its last payment is older
     * than one month (or it was never paid). PENSION applies to everybody; the other recurring
     * concepts (transport, food) apply only to students that have paid them at least once.
     */
    public List<DelinquentRecord> findDelinquentStudents(LocalDate today) {
        LocalDate limitDate = today.minusMonths(1);
        List<PaymentConcept> recurringConcepts = conceptDao.findAll().stream()
                .filter(concept -> concept.isRecurring() && concept.isActive())
                .toList();
        Map<Integer, List<Payment>> paymentsByStudent = paymentDao.findAll().stream()
                .collect(Collectors.groupingBy(payment -> payment.getStudentId()));
        List<Student> activeStudents = studentDao.findAll().stream()
                .filter(student -> student.getStatus() == StudentStatus.ACTIVE)
                .toList();

        List<DelinquentRecord> delinquents = new ArrayList<>();
        for (Student student : activeStudents) {
            List<Payment> studentPayments = paymentsByStudent.getOrDefault(student.getId(), new ArrayList<>());
            for (PaymentConcept concept : recurringConcepts) {
                LocalDate lastPayment = findLastPaymentDate(studentPayments, concept.getId());
                boolean applies = concept.getCode().equals(MANDATORY_CONCEPT_CODE) || lastPayment != null;
                if (applies && (lastPayment == null || lastPayment.isBefore(limitDate))) {
                    LocalDate since = lastPayment == null ? student.getAdmissionDate() : lastPayment;
                    delinquents.add(new DelinquentRecord(student.getId(), student.getFullName(), concept.getName(),
                            lastPayment, ChronoUnit.DAYS.between(since, today)));
                }
            }
        }
        return delinquents.stream()
                .sorted(Comparator.comparing((DelinquentRecord record) -> record.getDaysWithoutPayment()).reversed())
                .toList();
    }

    private LocalDate findLastPaymentDate(List<Payment> payments, int conceptId) {
        List<LocalDate> dates = payments.stream()
                .filter(payment -> payment.getConceptId() == conceptId)
                .map(payment -> payment.getPaymentDate())
                .sorted(Comparator.reverseOrder())
                .toList();
        return dates.isEmpty() ? null : dates.get(0);
    }

    private Guardian findLinkedGuardian(int studentId, Integer guardianId) {
        List<Guardian> linked = guardianDao.findByStudent(studentId).stream()
                .map(link -> link.getGuardian())
                .filter(guardian -> guardianId != null && guardian.getId() == guardianId)
                .toList();
        if (linked.isEmpty()) {
            throw new BusinessRuleException("El acudiente que paga debe estar vinculado al estudiante.");
        }
        return linked.get(0);
    }

    private Invoice buildInvoice(Payment payment, Student student, Guardian payer, PaymentConcept concept) {
        TaxStrategy taxStrategy = taxStrategyFactory.forConcept(concept.getCode());
        BigDecimal taxAmount = taxStrategy.calculateIncludedTax(payment.getAmount());
        Invoice invoice = new Invoice(payer, student.getFullName(), LocalDate.now());
        invoice.addLine(concept.getName() + " - " + student.getFullName(), 1, payment.getAmount().subtract(taxAmount));
        invoice.setTaxDescription(taxStrategy.getDescription());
        invoice.setTaxRate(taxStrategy.getRate());
        invoice.setTaxAmount(taxAmount);
        invoice.setPaidAmount(payment.getAmount());
        invoice.setPaymentMethod(payment.getMethod().getLabel());
        return invoice;
    }

    /** Runs inside the payment transaction, once the invoice number is known. */
    private void completeInvoice(Invoice invoice) {
        invoice.setCufe(CufeGenerator.generate(invoice.getInvoiceNumber(), SchoolInfo.NIT, invoice.getIssueDate(),
                invoice.getTotal()));
        String fileName = invoiceGenerator.buildFileName(invoice);
        invoice.setPdfFileName(fileName);
        invoice.setFilePath(invoiceGenerator.buildFilePath(fileName));
    }
}
