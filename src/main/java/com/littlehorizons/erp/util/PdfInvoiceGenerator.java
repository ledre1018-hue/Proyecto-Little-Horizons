package com.littlehorizons.erp.util;

import com.google.zxing.WriterException;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.InvoiceLine;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Creates the electronic invoice as a one-page PDF with OpenPDF.
 */
public class PdfInvoiceGenerator implements InvoiceDocumentGenerator {

    private static final String FILE_PREFIX = "factura_";
    private static final String FILE_EXTENSION = ".pdf";
    private static final int QR_SIZE = 140;
    private static final Color BRAND_COLOR = new Color(20, 66, 114);
    private static final Color LIGHT_GRAY = new Color(235, 239, 244);
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, BRAND_COLOR);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.DARK_GRAY);

    private final Path invoiceFolder;

    public PdfInvoiceGenerator() {
        this.invoiceFolder = Paths.get(SchoolInfo.INVOICE_FOLDER);
    }

    @Override
    public String buildFileName(Invoice invoice) {
        return FILE_PREFIX + invoice.getNumberDigits() + FILE_EXTENSION;
    }

    @Override
    public String buildFilePath(String fileName) {
        return invoiceFolder.resolve(fileName).toAbsolutePath().toString();
    }

    @Override
    public void generate(Invoice invoice) throws InvoiceGenerationException {
        try {
            Files.createDirectories(invoiceFolder);
            try (FileOutputStream output = new FileOutputStream(invoice.getFilePath())) {
                Document document = new Document(PageSize.LETTER, 40, 40, 36, 36);
                PdfWriter.getInstance(document, output);
                document.open();
                document.add(buildHeader(invoice));
                document.add(buildCustomerSection(invoice));
                document.add(buildLinesTable(invoice));
                document.add(buildTotalsTable(invoice));
                document.add(buildDianSection(invoice));
                document.add(new Paragraph("Documento generado por el ERP Little Horizons. Factura electrónica "
                        + "SIMULADA con fines académicos, sin validez fiscal.", SMALL_FONT));
                document.close();
            }
        } catch (IOException | DocumentException | WriterException e) {
            throw new InvoiceGenerationException("No fue posible generar el PDF de la factura "
                    + invoice.getInvoiceNumber() + ".", e);
        }
    }

    private PdfPTable buildHeader(Invoice invoice) {
        PdfPTable header = new PdfPTable(new float[]{3f, 2f});
        header.setWidthPercentage(100);
        PdfPCell schoolCell = new PdfPCell();
        schoolCell.setBorder(Rectangle.NO_BORDER);
        schoolCell.addElement(new Paragraph(SchoolInfo.LEGAL_NAME, TITLE_FONT));
        schoolCell.addElement(new Paragraph("NIT: " + SchoolInfo.NIT, BOLD_FONT));
        schoolCell.addElement(new Paragraph(SchoolInfo.ADDRESS, NORMAL_FONT));
        schoolCell.addElement(new Paragraph("Tel: " + SchoolInfo.PHONE + "  |  " + SchoolInfo.EMAIL, NORMAL_FONT));
        header.addCell(schoolCell);

        PdfPTable numberBox = new PdfPTable(1);
        numberBox.addCell(headerCell("FACTURA ELECTRÓNICA DE VENTA"));
        numberBox.addCell(valueCell("No. " + invoice.getInvoiceNumber(), BOLD_FONT, Element.ALIGN_CENTER));
        numberBox.addCell(valueCell("Fecha de emisión: " + invoice.getIssueDate(), NORMAL_FONT, Element.ALIGN_CENTER));
        numberBox.addCell(valueCell("Recibo de caja: " + invoice.getReceiptNumber(), NORMAL_FONT, Element.ALIGN_CENTER));
        PdfPCell boxCell = new PdfPCell(numberBox);
        boxCell.setBorder(Rectangle.NO_BORDER);
        header.addCell(boxCell);
        header.setSpacingAfter(14);
        return header;
    }

    private PdfPTable buildCustomerSection(Invoice invoice) {
        Guardian customer = invoice.getCustomer();
        PdfPTable table = new PdfPTable(new float[]{1.3f, 3f, 1.3f, 2.4f});
        table.setWidthPercentage(100);
        PdfPCell title = headerCell("DATOS DEL CLIENTE (ACUDIENTE RESPONSABLE DEL PAGO)");
        title.setColspan(4);
        table.addCell(title);
        table.addCell(labelCell("Nombre"));
        table.addCell(valueCell(customer.getFullName(), NORMAL_FONT, Element.ALIGN_LEFT));
        table.addCell(labelCell("Documento"));
        table.addCell(valueCell(customer.getDocumentType().getDbValue() + " " + customer.getDocumentNumber(),
                NORMAL_FONT, Element.ALIGN_LEFT));
        table.addCell(labelCell("Dirección"));
        table.addCell(valueCell(TextFormat.orDash(customer.getAddress()), NORMAL_FONT, Element.ALIGN_LEFT));
        table.addCell(labelCell("Estudiante"));
        table.addCell(valueCell(invoice.getStudentName(), NORMAL_FONT, Element.ALIGN_LEFT));
        table.addCell(labelCell("Teléfono"));
        table.addCell(valueCell(TextFormat.orDash(customer.getPhone()), NORMAL_FONT, Element.ALIGN_LEFT));
        table.addCell(labelCell("Medio de pago"));
        table.addCell(valueCell(invoice.getPaymentMethod(), NORMAL_FONT, Element.ALIGN_LEFT));
        table.setSpacingAfter(14);
        return table;
    }

    private PdfPTable buildLinesTable(Invoice invoice) {
        PdfPTable table = new PdfPTable(new float[]{4.5f, 1f, 2f, 2f});
        table.setWidthPercentage(100);
        table.addCell(headerCell("Descripción"));
        table.addCell(headerCell("Cantidad"));
        table.addCell(headerCell("Valor unitario"));
        table.addCell(headerCell("Subtotal"));
        for (InvoiceLine line : invoice.getLines()) {
            table.addCell(valueCell(line.getDescription(), NORMAL_FONT, Element.ALIGN_LEFT));
            table.addCell(valueCell(String.valueOf(line.getQuantity()), NORMAL_FONT, Element.ALIGN_CENTER));
            table.addCell(valueCell(TextFormat.money(line.getUnitPrice()), NORMAL_FONT, Element.ALIGN_RIGHT));
            table.addCell(valueCell(TextFormat.money(line.getSubtotal()), NORMAL_FONT, Element.ALIGN_RIGHT));
        }
        table.setSpacingAfter(8);
        return table;
    }

    private PdfPTable buildTotalsTable(Invoice invoice) {
        PdfPTable totals = new PdfPTable(new float[]{2.2f, 1.8f});
        totals.setWidthPercentage(45);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        addTotalRow(totals, "Subtotal", invoice.getSubtotal(), NORMAL_FONT);
        addTotalRow(totals, invoice.getTaxDescription(), invoice.getTaxAmount(), NORMAL_FONT);
        addTotalRow(totals, "TOTAL A PAGAR", invoice.getTotal(), BOLD_FONT);
        totals.setSpacingAfter(16);
        return totals;
    }

    private PdfPTable buildDianSection(Invoice invoice) throws WriterException, IOException, DocumentException {
        PdfPTable table = new PdfPTable(new float[]{3.2f, 1.3f});
        table.setWidthPercentage(100);
        PdfPCell title = headerCell("INFORMACIÓN DIAN (SIMULADA)");
        title.setColspan(2);
        table.addCell(title);

        PdfPCell textCell = new PdfPCell();
        textCell.setPadding(6);
        textCell.addElement(new Paragraph("CUFE:", BOLD_FONT));
        textCell.addElement(new Paragraph(invoice.getCufe(), SMALL_FONT));
        textCell.addElement(new Paragraph(" ", SMALL_FONT));
        textCell.addElement(new Paragraph("Resolución de habilitación de facturación electrónica No. "
                + SchoolInfo.RESOLUTION_NUMBER + " del " + SchoolInfo.RESOLUTION_DATE + ". Rango autorizado: "
                + SchoolInfo.RESOLUTION_RANGE + ". Vigencia: 24 meses.", NORMAL_FONT));
        textCell.addElement(new Paragraph("Resolución SIMULADA para fines académicos.", SMALL_FONT));
        table.addCell(textCell);

        Image qrImage = Image.getInstance(QrCodeGenerator.generatePng(buildQrContent(invoice), QR_SIZE));
        PdfPCell qrCell = new PdfPCell(qrImage, true);
        qrCell.setPadding(6);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(qrCell);
        table.setSpacingAfter(12);
        return table;
    }

    private String buildQrContent(Invoice invoice) {
        return "NumFac: " + invoice.getInvoiceNumber()
                + "\nFecFac: " + invoice.getIssueDate()
                + "\nNitFac: " + SchoolInfo.NIT
                + "\nDocAdq: " + invoice.getCustomer().getDocumentNumber()
                + "\nValFac: " + invoice.getSubtotal().toPlainString()
                + "\nValIva: " + invoice.getTaxAmount().toPlainString()
                + "\nValTotal: " + invoice.getTotal().toPlainString()
                + "\nCUFE: " + invoice.getCufe();
    }

    private void addTotalRow(PdfPTable table, String label, BigDecimal amount, Font font) {
        PdfPCell labelCell = valueCell(label, font, Element.ALIGN_LEFT);
        labelCell.setBackgroundColor(LIGHT_GRAY);
        table.addCell(labelCell);
        table.addCell(valueCell(TextFormat.money(amount), font, Element.ALIGN_RIGHT));
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(BRAND_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell labelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BOLD_FONT));
        cell.setBackgroundColor(LIGHT_GRAY);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell valueCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        return cell;
    }
}
