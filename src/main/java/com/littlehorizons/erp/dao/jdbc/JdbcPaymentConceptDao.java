package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.PaymentConceptDao;
import com.littlehorizons.erp.model.PaymentConcept;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcPaymentConceptDao extends BaseJdbcDao implements PaymentConceptDao {

    private static final String SELECT_COLUMNS = "SELECT id_concepto, codigo_concepto, nombre_concepto, "
            + "descripcion, monto_base, es_recurrente, esta_activo FROM conceptos_pago ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY id_concepto";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE id_concepto = ?";
    private static final String INSERT = "INSERT INTO conceptos_pago (codigo_concepto, nombre_concepto, "
            + "descripcion, monto_base, es_recurrente, esta_activo) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE conceptos_pago SET codigo_concepto = ?, nombre_concepto = ?, "
            + "descripcion = ?, monto_base = ?, es_recurrente = ?, esta_activo = ? WHERE id_concepto = ?";
    private static final String DELETE = "DELETE FROM conceptos_pago WHERE id_concepto = ?";

    @Override
    public List<PaymentConcept> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapConcept(resultSet));
    }

    @Override
    public PaymentConcept findById(int conceptId) {
        return queryForObject(FIND_BY_ID, resultSet -> mapConcept(resultSet), conceptId);
    }

    @Override
    public int insert(PaymentConcept concept) {
        return executeInsert(INSERT, concept.getCode(), concept.getName(), concept.getDescription(),
                concept.getBaseAmount(), concept.isRecurring(), concept.isActive());
    }

    @Override
    public void update(PaymentConcept concept) {
        executeUpdate(UPDATE, concept.getCode(), concept.getName(), concept.getDescription(),
                concept.getBaseAmount(), concept.isRecurring(), concept.isActive(), concept.getId());
    }

    @Override
    public void delete(int conceptId) {
        executeUpdate(DELETE, conceptId);
    }

    private PaymentConcept mapConcept(ResultSet resultSet) throws SQLException {
        PaymentConcept concept = new PaymentConcept(resultSet.getString("codigo_concepto"),
                resultSet.getString("nombre_concepto"), resultSet.getBigDecimal("monto_base"),
                resultSet.getBoolean("es_recurrente"));
        concept.setId(resultSet.getInt("id_concepto"));
        concept.setDescription(resultSet.getString("descripcion"));
        concept.setActive(resultSet.getBoolean("esta_activo"));
        return concept;
    }
}
