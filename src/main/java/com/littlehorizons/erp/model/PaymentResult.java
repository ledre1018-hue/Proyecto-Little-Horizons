package com.littlehorizons.erp.model;

/**
 * What happened when a payment was registered: the saved payment, its invoice
 * and whether the PDF file could be created.
 */
public class PaymentResult {

    private final Payment payment;
    private final Invoice invoice;
    private final boolean pdfGenerated;

    public PaymentResult(Payment payment, Invoice invoice, boolean pdfGenerated) {
        this.payment = payment;
        this.invoice = invoice;
        this.pdfGenerated = pdfGenerated;
    }

    public Payment getPayment() {
        return payment;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public boolean isPdfGenerated() {
        return pdfGenerated;
    }
}
