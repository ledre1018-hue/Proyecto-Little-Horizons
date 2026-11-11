package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.EnrollmentDao;
import com.littlehorizons.erp.model.Enrollment;
import com.littlehorizons.erp.model.EnrollmentStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JdbcEnrollmentDao extends BaseJdbcDao implements EnrollmentDao {

    private static final String SELECT_COLUMNS = "SELECT m.id_matricula, m.id_estudiante, m.id_grado, "
            + "g.nombre_grado, m.año_academico, m.estado_matricula, m.fecha_matricula, m.fecha_retiro, "
            + "m.observaciones FROM matriculas m JOIN grados g ON g.id_grado = m.id_grado ";
    private static final String FIND_BY_STUDENT = SELECT_COLUMNS + "WHERE m.id_estudiante = ? "
            + "ORDER BY m.año_academico";
    private static final String FIND_BY_YEAR = SELECT_COLUMNS + "WHERE m.año_academico = ?";
    private static final String INSERT = "INSERT INTO matriculas (id_estudiante, id_grado, año_academico, "
            + "estado_matricula, fecha_matricula, observaciones) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_STATUS = "UPDATE matriculas SET estado_matricula = ?, fecha_retiro = ? "
            + "WHERE id_matricula = ?";

    @Override
    public List<Enrollment> findByStudent(int studentId) {
        return queryForList(FIND_BY_STUDENT, resultSet -> mapEnrollment(resultSet), studentId);
    }

    @Override
    public List<Enrollment> findByYear(int academicYear) {
        return queryForList(FIND_BY_YEAR, resultSet -> mapEnrollment(resultSet), academicYear);
    }

    @Override
    public int insert(Enrollment enrollment) {
        return executeInsert(INSERT, enrollment.getStudentId(), enrollment.getGradeId(),
                enrollment.getAcademicYear(), enrollment.getStatus().getDbValue(), enrollment.getEnrollmentDate(),
                enrollment.getNotes());
    }

    @Override
    public void updateStatus(int enrollmentId, EnrollmentStatus status, LocalDate withdrawalDate) {
        executeUpdate(UPDATE_STATUS, status.getDbValue(), withdrawalDate, enrollmentId);
    }

    private Enrollment mapEnrollment(ResultSet resultSet) throws SQLException {
        Enrollment enrollment = new Enrollment(resultSet.getInt("id_estudiante"), resultSet.getInt("id_grado"),
                resultSet.getInt("año_academico"), getDate(resultSet, "fecha_matricula"));
        enrollment.setId(resultSet.getInt("id_matricula"));
        enrollment.setGradeName(resultSet.getString("nombre_grado"));
        enrollment.setStatus(EnrollmentStatus.fromDbValue(resultSet.getString("estado_matricula")));
        enrollment.setWithdrawalDate(getDate(resultSet, "fecha_retiro"));
        enrollment.setNotes(resultSet.getString("observaciones"));
        return enrollment;
    }
}
