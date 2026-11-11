package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.PaymentDao;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentMethod;
import com.littlehorizons.erp.model.PaymentPeriod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class JdbcPaymentDao extends BaseJdbcDao implements PaymentDao {

    private static final String RECEIPT_PREFIX = "RC-";
    private static final String INVOICE_PREFIX = "FE-";
    private static final String SELECT_COLUMNS = "SELECT p.id_pago, p.id_estudiante, "
            + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_estudiante, p.id_acudiente, "
            + "CONCAT(a.primer_nombre, ' ', a.primer_apellido) AS nombre_acudiente, p.id_concepto, "
            + "c.nombre_concepto, p.monto_pagado, p.fecha_pago, p.metodo_pago, p.numero_recibo, p.año_academico, "
            + "p.periodo_academico, p.notas FROM pagos p JOIN estudiantes e ON e.id_estudiante = p.id_estudiante "
            + "LEFT JOIN acudientes a ON a.id_acudiente = p.id_acudiente "
            + "JOIN conceptos_pago c ON c.id_concepto = p.id_concepto ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY p.fecha_pago, p.id_pago";
    private static final String FIND_BY_STUDENT = SELECT_COLUMNS + "WHERE p.id_estudiante = ? "
            + "ORDER BY p.fecha_pago DESC, p.id_pago DESC";
    // FOR UPDATE locks the rows read, so two payments in parallel cannot get the same number
    private static final String NEXT_RECEIPT = "SELECT COALESCE(MAX(CAST(SUBSTRING(numero_recibo, 4) AS UNSIGNED)), "
            + "0) + 1 FROM pagos WHERE numero_recibo LIKE 'RC-%' FOR UPDATE";
    private static final String NEXT_INVOICE = "SELECT COALESCE(MAX(CAST(SUBSTRING(numero_factura, 4) AS UNSIGNED)), "
            + "0) + 1 FROM facturas WHERE numero_factura LIKE 'FE-%' FOR UPDATE";
    private static final String INSERT_PAYMENT = "INSERT INTO pagos (id_estudiante, id_acudiente, id_concepto, "
            + "monto_pagado, fecha_pago, metodo_pago, numero_recibo, año_academico, periodo_academico, notas) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_INVOICE = "INSERT INTO facturas (numero_factura, id_pago, fecha_emision, "
            + "cufe, nombre_archivo_pdf, ruta_archivo) VALUES (?, ?, ?, ?, ?, ?)";

    @Override
    public List<Payment> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapPayment(resultSet));
    }

    @Override
    public List<Payment> findByStudent(int studentId) {
        return queryForList(FIND_BY_STUDENT, resultSet -> mapPayment(resultSet), studentId);
    }

    @Override
    public void insertWithInvoice(Payment payment, Invoice invoice, Consumer<Invoice> invoiceCompleter) {
        String source = "JdbcPaymentDao.insertWithInvoice";
        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                payment.setReceiptNumber(nextNumber(connection, NEXT_RECEIPT, RECEIPT_PREFIX));
                String period = payment.getPeriod() == null ? null : payment.getPeriod().getDbValue();
                int paymentId = insertAndGetId(connection, INSERT_PAYMENT, payment.getStudentId(),
                        payment.getGuardianId(), payment.getConceptId(), payment.getAmount(), payment.getPaymentDate(),
                        payment.getMethod().getDbValue(), payment.getReceiptNumber(), payment.getAcademicYear(),
                        period, payment.getNotes());
                payment.setId(paymentId);

                invoice.setPaymentId(paymentId);
                invoice.setReceiptNumber(payment.getReceiptNumber());
                invoice.setInvoiceNumber(nextNumber(connection, NEXT_INVOICE, INVOICE_PREFIX));
                invoiceCompleter.accept(invoice);
                int invoiceId = insertAndGetId(connection, INSERT_INVOICE, invoice.getInvoiceNumber(), paymentId,
                        invoice.getIssueDate(), invoice.getCufe(), invoice.getPdfFileName(), invoice.getFilePath());
                invoice.setId(invoiceId);
                connection.commit();
            } catch (SQLException e) {
                rollbackQuietly(connection, source);
                throw e;
            }
        } catch (SQLException e) {
            throw handleError(source, e);
        }
    }

    private String nextNumber(Connection connection, String sql, String prefix) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return String.format("%s%06d", prefix, resultSet.getInt(1));
        }
    }

    private Payment mapPayment(ResultSet resultSet) throws SQLException {
        Payment payment = new Payment();
        payment.setId(resultSet.getInt("id_pago"));
        payment.setStudentId(resultSet.getInt("id_estudiante"));
        payment.setStudentName(resultSet.getString("nombre_estudiante"));
        payment.setGuardianId(getNullableInt(resultSet, "id_acudiente"));
        payment.setGuardianName(resultSet.getString("nombre_acudiente"));
        payment.setConceptId(resultSet.getInt("id_concepto"));
        payment.setConceptName(resultSet.getString("nombre_concepto"));
        payment.setAmount(resultSet.getBigDecimal("monto_pagado"));
        payment.setPaymentDate(getDate(resultSet, "fecha_pago"));
        payment.setMethod(PaymentMethod.fromDbValue(resultSet.getString("metodo_pago")));
        payment.setReceiptNumber(resultSet.getString("numero_recibo"));
        payment.setAcademicYear(getNullableInt(resultSet, "año_academico"));
        String period = resultSet.getString("periodo_academico");
        payment.setPeriod(period == null ? null : PaymentPeriod.fromDbValue(period));
        payment.setNotes(resultSet.getString("notas"));
        return payment;
    }
}
