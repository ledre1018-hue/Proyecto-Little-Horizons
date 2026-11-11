package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.SubjectDao;
import com.littlehorizons.erp.model.Subject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcSubjectDao extends BaseJdbcDao implements SubjectDao {

    private static final String SELECT_COLUMNS = "SELECT m.id_materia, m.codigo_materia, m.nombre_materia, "
            + "m.descripcion, m.id_grado, g.nombre_grado, m.horas_semanales, m.esta_activa "
            + "FROM materias m JOIN grados g ON g.id_grado = m.id_grado ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY g.nivel_numerico, m.nombre_materia";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE m.id_materia = ?";

    @Override
    public List<Subject> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapSubject(resultSet));
    }

    @Override
    public Subject findById(int subjectId) {
        return queryForObject(FIND_BY_ID, resultSet -> mapSubject(resultSet), subjectId);
    }

    private Subject mapSubject(ResultSet resultSet) throws SQLException {
        Subject subject = new Subject(resultSet.getString("codigo_materia"), resultSet.getString("nombre_materia"),
                resultSet.getInt("id_grado"), resultSet.getInt("horas_semanales"));
        subject.setId(resultSet.getInt("id_materia"));
        subject.setDescription(resultSet.getString("descripcion"));
        subject.setGradeName(resultSet.getString("nombre_grado"));
        subject.setActive(resultSet.getBoolean("esta_activa"));
        return subject;
    }
}
