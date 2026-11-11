package com.littlehorizons.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Electronic invoice of a payment (table facturas).
 * Its lines are created inside the invoice with addLine (composition).
 */
public class Invoice {

    private int id;
    private String invoiceNumber;
    private int paymentId;
    private LocalDate issueDate;
    private String cufe;
    private String pdfFileName;
    private String filePath;
    private Guardian customer;
    private String studentName;
    private String receiptNumber;
    private String paymentMethod;
    private String taxDescription;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal paidAmount;
    private final List<InvoiceLine> lines;

    public Invoice() {
        this.lines = new ArrayList<>();
        this.taxRate = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
    }

    public Invoice(Guardian customer, String studentName, LocalDate issueDate) {
        this();
        this.customer = customer;
        this.studentName = studentName;
        this.issueDate = issueDate;
    }

    public void addLine(String description, int quantity, BigDecimal unitPrice) {
        lines.add(new InvoiceLine(description, quantity, unitPrice));
    }

    public List<InvoiceLine> getLines() {
        return new ArrayList<>(lines);
    }

    public BigDecimal getSubtotal() {
        return lines.stream()
                .map(line -> line.getSubtotal())
                .reduce(BigDecimal.ZERO, (total, lineSubtotal) -> total.add(lineSubtotal));
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(taxAmount);
    }

    /** Number without prefix, e.g. FE-000123 -> 000123. Used for the PDF file name. */
    public String getNumberDigits() {
        return invoiceNumber.substring(invoiceNumber.indexOf('-') + 1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getCufe() {
        return cufe;
    }

    public void setCufe(String cufe) {
        this.cufe = cufe;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Guardian getCustomer() {
        return customer;
    }

    public void setCustomer(Guardian customer) {
        this.customer = customer;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTaxDescription() {
        return taxDescription;
    }

    public void setTaxDescription(String taxDescription) {
        this.taxDescription = taxDescription;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
}
