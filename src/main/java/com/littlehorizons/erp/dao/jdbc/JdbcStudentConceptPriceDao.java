package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.StudentConceptPriceDao;
import com.littlehorizons.erp.model.StudentConceptPrice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcStudentConceptPriceDao extends BaseJdbcDao implements StudentConceptPriceDao {

    private static final String SELECT_COLUMNS = "SELECT v.id_valor, v.id_estudiante, "
            + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_estudiante, v.id_concepto, "
            + "c.nombre_concepto, v.año_academico, v.monto_personalizado, v.motivo "
            + "FROM valores_concepto_estudiante v JOIN estudiantes e ON e.id_estudiante = v.id_estudiante "
            + "JOIN conceptos_pago c ON c.id_concepto = v.id_concepto ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY v.año_academico DESC, e.primer_apellido";
    private static final String FIND_BY_STUDENT_AND_CONCEPT = SELECT_COLUMNS
            + "WHERE v.id_estudiante = ? AND v.id_concepto = ? AND v.año_academico = ?";
    private static final String INSERT = "INSERT INTO valores_concepto_estudiante (id_estudiante, id_concepto, "
            + "año_academico, monto_personalizado, motivo) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM valores_concepto_estudiante WHERE id_valor = ?";

    @Override
    public List<StudentConceptPrice> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapPrice(resultSet));
    }

    @Override
    public StudentConceptPrice findByStudentAndConcept(int studentId, int conceptId, int academicYear) {
        return queryForObject(FIND_BY_STUDENT_AND_CONCEPT, resultSet -> mapPrice(resultSet), studentId, conceptId,
                academicYear);
    }

    @Override
    public int insert(StudentConceptPrice price) {
        return executeInsert(INSERT, price.getStudentId(), price.getConceptId(), price.getAcademicYear(),
                price.getCustomAmount(), price.getReason());
    }

    @Override
    public void delete(int priceId) {
        executeUpdate(DELETE, priceId);
    }

    private StudentConceptPrice mapPrice(ResultSet resultSet) throws SQLException {
        StudentConceptPrice price = new StudentConceptPrice(resultSet.getInt("id_estudiante"),
                resultSet.getInt("id_concepto"), resultSet.getInt("año_academico"),
                resultSet.getBigDecimal("monto_personalizado"), resultSet.getString("motivo"));
        price.setId(resultSet.getInt("id_valor"));
        price.setStudentName(resultSet.getString("nombre_estudiante"));
        price.setConceptName(resultSet.getString("nombre_concepto"));
        return price;
    }
}
