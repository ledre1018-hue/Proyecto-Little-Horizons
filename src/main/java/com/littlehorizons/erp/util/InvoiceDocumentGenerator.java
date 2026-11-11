package com.littlehorizons.erp.util;

import com.littlehorizons.erp.model.Invoice;

/**
 * Contract to create the document of an invoice. The controller depends on this interface,
 * not on the PDF library.
 */
public interface InvoiceDocumentGenerator {

    /** Unique file name, for example factura_000123.pdf. */
    String buildFileName(Invoice invoice);

    /** Full path where the file will be saved. */
    String buildFilePath(String fileName);

    void generate(Invoice invoice) throws InvoiceGenerationException;
}
