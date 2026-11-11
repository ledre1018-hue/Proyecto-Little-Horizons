package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.model.TeacherLoan;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcTeacherLoanDao extends JdbcLoanDao<TeacherLoan> {

    private static final String SELECT_COLUMNS = "SELECT p.id_prestamo, p.id_recurso, "
            + "CONCAT(r.codigo_recurso, ' ', r.nombre_recurso) AS nombre_recurso, p.id_profesor, "
            + "CONCAT(t.primer_nombre, ' ', t.primer_apellido) AS nombre_prestatario, p.fecha_prestamo, "
            + "p.fecha_devolucion_esperada, p.fecha_devolucion_real, p.estado_prestamo, p.observaciones "
            + "FROM prestamos_profesor p JOIN recursos r ON r.id_recurso = p.id_recurso "
            + "JOIN profesores t ON t.id_profesor = p.id_profesor ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY p.fecha_prestamo DESC";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE p.id_prestamo = ?";
    private static final String INSERT = "INSERT INTO prestamos_profesor (id_recurso, id_profesor, "
            + "fecha_prestamo, fecha_devolucion_esperada, estado_prestamo, observaciones) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String RETURN = "UPDATE prestamos_profesor SET estado_prestamo = ?, "
            + "fecha_devolucion_real = ?, observaciones = ? WHERE id_prestamo = ?";
    private static final String MARK_OVERDUE = "UPDATE prestamos_profesor SET estado_prestamo = ? "
            + "WHERE estado_prestamo = ? AND fecha_devolucion_esperada < ?";

    @Override
    protected String findAllSql() {
        return FIND_ALL;
    }

    @Override
    protected String findByIdSql() {
        return FIND_BY_ID;
    }

    @Override
    protected String insertSql() {
        return INSERT;
    }

    @Override
    protected String returnSql() {
        return RETURN;
    }

    @Override
    protected String markOverdueSql() {
        return MARK_OVERDUE;
    }

    @Override
    protected TeacherLoan mapLoan(ResultSet resultSet) throws SQLException {
        TeacherLoan loan = new TeacherLoan();
        mapCommonColumns(resultSet, loan);
        loan.setTeacherId(resultSet.getInt("id_profesor"));
        return loan;
    }
}
