package com.littlehorizons.erp.util;

/**
 * The invoice document (PDF) could not be created.
 */
public class InvoiceGenerationException extends Exception {

    public InvoiceGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
