package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.ClassScheduleDao;
import com.littlehorizons.erp.model.ClassSchedule;
import com.littlehorizons.erp.model.WeekDay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class JdbcClassScheduleDao extends BaseJdbcDao implements ClassScheduleDao {

    private static final String FIND_BY_TEACHER = "SELECT h.id_horario, h.id_asignacion, a.id_profesor, "
            + "CONCAT(m.nombre_materia, ' (', g.nombre_grado, ' ', a.seccion, ')') AS nombre_materia, "
            + "h.dia_semana, h.hora_inicio, h.hora_fin FROM horarios_clase h "
            + "JOIN asignaciones_profesor a ON a.id_asignacion = h.id_asignacion "
            + "JOIN materias m ON m.id_materia = a.id_materia JOIN grados g ON g.id_grado = m.id_grado "
            + "WHERE a.id_profesor = ? AND a.año_academico = ? ORDER BY h.dia_semana, h.hora_inicio";
    private static final String INSERT = "INSERT INTO horarios_clase (id_asignacion, dia_semana, hora_inicio, "
            + "hora_fin) VALUES (?, ?, ?, ?)";

    @Override
    public List<ClassSchedule> findByTeacher(int teacherId, int academicYear) {
        return queryForList(FIND_BY_TEACHER, resultSet -> mapSchedule(resultSet), teacherId, academicYear);
    }

    @Override
    public int insert(ClassSchedule schedule) {
        return executeInsert(INSERT, schedule.getAssignmentId(), schedule.getDay().getDbValue(),
                schedule.getStartTime(), schedule.getEndTime());
    }

    private ClassSchedule mapSchedule(ResultSet resultSet) throws SQLException {
        ClassSchedule schedule = new ClassSchedule(resultSet.getInt("id_asignacion"),
                WeekDay.fromDbValue(resultSet.getString("dia_semana")),
                resultSet.getObject("hora_inicio", LocalTime.class), resultSet.getObject("hora_fin", LocalTime.class));
        schedule.setId(resultSet.getInt("id_horario"));
        schedule.setTeacherId(resultSet.getInt("id_profesor"));
        schedule.setSubjectName(resultSet.getString("nombre_materia"));
        return schedule;
    }
}
