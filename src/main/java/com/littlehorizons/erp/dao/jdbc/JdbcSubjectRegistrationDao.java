package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.SubjectRegistrationDao;
import com.littlehorizons.erp.model.AcademicPeriod;
import com.littlehorizons.erp.model.EnrollmentStatus;
import com.littlehorizons.erp.model.SubjectRegistration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcSubjectRegistrationDao extends BaseJdbcDao implements SubjectRegistrationDao {

    private static final String FIND_BY_STUDENT = "SELECT i.id_inscripcion, i.id_estudiante, i.id_materia, "
            + "m.nombre_materia, i.año_academico, i.periodo_academico, i.seccion, i.fecha_inscripcion, i.estado "
            + "FROM inscripciones_materia i JOIN materias m ON m.id_materia = i.id_materia "
            + "WHERE i.id_estudiante = ? ORDER BY i.año_academico, i.periodo_academico, m.nombre_materia";
    private static final String INSERT = "INSERT INTO inscripciones_materia (id_estudiante, id_materia, "
            + "año_academico, periodo_academico, seccion, fecha_inscripcion, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Override
    public List<SubjectRegistration> findByStudent(int studentId) {
        return queryForList(FIND_BY_STUDENT, resultSet -> mapRegistration(resultSet), studentId);
    }

    @Override
    public int insert(SubjectRegistration registration) {
        return executeInsert(INSERT, registration.getStudentId(), registration.getSubjectId(),
                registration.getAcademicYear(), registration.getPeriod().getDbValue(), registration.getSection(),
                registration.getRegistrationDate(), registration.getStatus().getDbValue());
    }

    private SubjectRegistration mapRegistration(ResultSet resultSet) throws SQLException {
        SubjectRegistration registration = new SubjectRegistration(resultSet.getInt("id_estudiante"),
                resultSet.getInt("id_materia"), resultSet.getInt("año_academico"),
                AcademicPeriod.fromDbValue(resultSet.getString("periodo_academico")),
                getDate(resultSet, "fecha_inscripcion"));
        registration.setId(resultSet.getInt("id_inscripcion"));
        registration.setSubjectName(resultSet.getString("nombre_materia"));
        registration.setSection(resultSet.getString("seccion"));
        registration.setStatus(EnrollmentStatus.fromDbValue(resultSet.getString("estado")));
        return registration;
    }
}
