package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.AttendanceDao;
import com.littlehorizons.erp.model.Attendance;
import com.littlehorizons.erp.model.AttendanceStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcAttendanceDao extends BaseJdbcDao implements AttendanceDao {

    private static final String SELECT_COLUMNS = "SELECT a.id_asistencia, a.id_estudiante, "
            + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_estudiante, a.id_materia, "
            + "m.nombre_materia, a.fecha_asistencia, a.estado_asistencia, a.observaciones, a.registrado_por "
            + "FROM asistencias a JOIN estudiantes e ON e.id_estudiante = a.id_estudiante "
            + "JOIN materias m ON m.id_materia = a.id_materia ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY a.fecha_asistencia";
    private static final String FIND_BY_STUDENT = SELECT_COLUMNS + "WHERE a.id_estudiante = ? "
            + "ORDER BY a.fecha_asistencia DESC";
    private static final String INSERT = "INSERT INTO asistencias (id_estudiante, id_materia, fecha_asistencia, "
            + "estado_asistencia, observaciones, registrado_por) VALUES (?, ?, ?, ?, ?, ?)";

    @Override
    public List<Attendance> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapAttendance(resultSet));
    }

    @Override
    public List<Attendance> findByStudent(int studentId) {
        return queryForList(FIND_BY_STUDENT, resultSet -> mapAttendance(resultSet), studentId);
    }

    @Override
    public int insert(Attendance attendance) {
        return executeInsert(INSERT, attendance.getStudentId(), attendance.getSubjectId(), attendance.getDate(),
                attendance.getStatus().getDbValue(), attendance.getNotes(), attendance.getRecordedBy());
    }

    private Attendance mapAttendance(ResultSet resultSet) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setId(resultSet.getInt("id_asistencia"));
        attendance.setStudentId(resultSet.getInt("id_estudiante"));
        attendance.setStudentName(resultSet.getString("nombre_estudiante"));
        attendance.setSubjectId(resultSet.getInt("id_materia"));
        attendance.setSubjectName(resultSet.getString("nombre_materia"));
        attendance.setDate(getDate(resultSet, "fecha_asistencia"));
        attendance.setStatus(AttendanceStatus.fromDbValue(resultSet.getString("estado_asistencia")));
        attendance.setNotes(resultSet.getString("observaciones"));
        attendance.setRecordedBy(getNullableInt(resultSet, "registrado_por"));
        return attendance;
    }
}
