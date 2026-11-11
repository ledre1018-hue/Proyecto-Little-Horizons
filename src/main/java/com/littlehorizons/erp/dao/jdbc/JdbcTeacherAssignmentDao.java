package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.TeacherAssignmentDao;
import com.littlehorizons.erp.model.TeacherAssignment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcTeacherAssignmentDao extends BaseJdbcDao implements TeacherAssignmentDao {

    private static final String SELECT_COLUMNS = "SELECT a.id_asignacion, a.id_profesor, "
            + "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nombre_profesor, a.id_materia, "
            + "CONCAT(m.nombre_materia, ' (', g.nombre_grado, ')') AS nombre_materia, a.año_academico, a.seccion "
            + "FROM asignaciones_profesor a JOIN profesores p ON p.id_profesor = a.id_profesor "
            + "JOIN materias m ON m.id_materia = a.id_materia JOIN grados g ON g.id_grado = m.id_grado ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY a.año_academico DESC, p.primer_apellido";
    private static final String FIND_BY_TEACHER = SELECT_COLUMNS + "WHERE a.id_profesor = ? "
            + "ORDER BY a.año_academico DESC, g.nivel_numerico";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE a.id_asignacion = ?";
    private static final String INSERT = "INSERT INTO asignaciones_profesor (id_profesor, id_materia, "
            + "año_academico, seccion) VALUES (?, ?, ?, ?)";

    @Override
    public List<TeacherAssignment> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapAssignment(resultSet));
    }

    @Override
    public List<TeacherAssignment> findByTeacher(int teacherId) {
        return queryForList(FIND_BY_TEACHER, resultSet -> mapAssignment(resultSet), teacherId);
    }

    @Override
    public TeacherAssignment findById(int assignmentId) {
        return queryForObject(FIND_BY_ID, resultSet -> mapAssignment(resultSet), assignmentId);
    }

    @Override
    public int insert(TeacherAssignment assignment) {
        return executeInsert(INSERT, assignment.getTeacherId(), assignment.getSubjectId(),
                assignment.getAcademicYear(), assignment.getSection());
    }

    private TeacherAssignment mapAssignment(ResultSet resultSet) throws SQLException {
        TeacherAssignment assignment = new TeacherAssignment(resultSet.getInt("id_profesor"),
                resultSet.getInt("id_materia"), resultSet.getInt("año_academico"), resultSet.getString("seccion"));
        assignment.setId(resultSet.getInt("id_asignacion"));
        assignment.setTeacherName(resultSet.getString("nombre_profesor"));
        assignment.setSubjectName(resultSet.getString("nombre_materia"));
        return assignment;
    }
}
