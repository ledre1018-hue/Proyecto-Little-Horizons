package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.AcademicRecordDao;
import com.littlehorizons.erp.model.AcademicPeriod;
import com.littlehorizons.erp.model.AcademicRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcAcademicRecordDao extends BaseJdbcDao implements AcademicRecordDao {

    private static final String SELECT_COLUMNS = "SELECT r.id_registro, r.id_estudiante, "
            + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_estudiante, r.id_materia, "
            + "m.nombre_materia, m.id_grado, g.nombre_grado, r.id_profesor, r.año_academico, r.periodo_academico, "
            + "r.valor_nota, r.observaciones, r.fecha_registro FROM registros_academicos r "
            + "JOIN estudiantes e ON e.id_estudiante = r.id_estudiante "
            + "JOIN materias m ON m.id_materia = r.id_materia JOIN grados g ON g.id_grado = m.id_grado ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY r.año_academico, r.id_estudiante";
    private static final String FIND_BY_STUDENT = SELECT_COLUMNS + "WHERE r.id_estudiante = ? "
            + "ORDER BY r.año_academico, m.nombre_materia, r.periodo_academico";
    private static final String INSERT = "INSERT INTO registros_academicos (id_estudiante, id_materia, "
            + "id_profesor, año_academico, periodo_academico, valor_nota, observaciones, fecha_registro) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public List<AcademicRecord> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapRecord(resultSet));
    }

    @Override
    public List<AcademicRecord> findByStudent(int studentId) {
        return queryForList(FIND_BY_STUDENT, resultSet -> mapRecord(resultSet), studentId);
    }

    @Override
    public int insert(AcademicRecord record) {
        return executeInsert(INSERT, record.getStudentId(), record.getSubjectId(), record.getTeacherId(),
                record.getAcademicYear(), record.getPeriod().getDbValue(), record.getValue(), record.getNotes(),
                record.getRecordDate());
    }

    private AcademicRecord mapRecord(ResultSet resultSet) throws SQLException {
        AcademicRecord record = new AcademicRecord(resultSet.getInt("id_estudiante"), resultSet.getInt("id_materia"),
                resultSet.getInt("id_profesor"), resultSet.getInt("año_academico"),
                AcademicPeriod.fromDbValue(resultSet.getString("periodo_academico")),
                resultSet.getDouble("valor_nota"));
        record.setId(resultSet.getInt("id_registro"));
        record.setStudentName(resultSet.getString("nombre_estudiante"));
        record.setSubjectName(resultSet.getString("nombre_materia"));
        record.setGradeId(resultSet.getInt("id_grado"));
        record.setGradeName(resultSet.getString("nombre_grado"));
        record.setNotes(resultSet.getString("observaciones"));
        record.setRecordDate(getDate(resultSet, "fecha_registro"));
        return record;
    }
}
