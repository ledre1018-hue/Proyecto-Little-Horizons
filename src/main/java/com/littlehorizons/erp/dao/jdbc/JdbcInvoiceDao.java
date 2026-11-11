package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.InvoiceDao;
import com.littlehorizons.erp.model.Invoice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcInvoiceDao extends BaseJdbcDao implements InvoiceDao {

    private static final String FIND_ALL = "SELECT f.id_factura, f.numero_factura, f.id_pago, f.fecha_emision, "
            + "f.cufe, f.nombre_archivo_pdf, f.ruta_archivo, p.numero_recibo, p.monto_pagado, "
            + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_estudiante FROM facturas f "
            + "JOIN pagos p ON p.id_pago = f.id_pago JOIN estudiantes e ON e.id_estudiante = p.id_estudiante "
            + "ORDER BY f.id_factura DESC";

    @Override
    public List<Invoice> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapInvoice(resultSet));
    }

    private Invoice mapInvoice(ResultSet resultSet) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(resultSet.getInt("id_factura"));
        invoice.setInvoiceNumber(resultSet.getString("numero_factura"));
        invoice.setPaymentId(resultSet.getInt("id_pago"));
        invoice.setIssueDate(getDate(resultSet, "fecha_emision"));
        invoice.setCufe(resultSet.getString("cufe"));
        invoice.setPdfFileName(resultSet.getString("nombre_archivo_pdf"));
        invoice.setFilePath(resultSet.getString("ruta_archivo"));
        invoice.setReceiptNumber(resultSet.getString("numero_recibo"));
        invoice.setPaidAmount(resultSet.getBigDecimal("monto_pagado"));
        invoice.setStudentName(resultSet.getString("nombre_estudiante"));
        return invoice;
    }
}
