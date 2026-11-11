package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;

import java.util.List;
import java.util.function.Consumer;

public interface PaymentDao {

    List<Payment> findAll();

    List<Payment> findByStudent(int studentId);

    /**
     * Saves the payment and its invoice in one transaction. The receipt and invoice numbers are
     * calculated inside the transaction; then invoiceCompleter fills the data that depends on
     * the invoice number (CUFE and file name) before the invoice row is saved.
     */
    void insertWithInvoice(Payment payment, Invoice invoice, Consumer<Invoice> invoiceCompleter);
}
