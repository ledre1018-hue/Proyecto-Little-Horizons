package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.EventParticipantDao;
import com.littlehorizons.erp.model.CompetitionPosition;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.ParticipationRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcEventParticipantDao extends BaseJdbcDao implements EventParticipantDao {

    private static final String SELECT_COLUMNS = "SELECT pe.id_participacion, pe.id_evento, ev.nombre_evento, "
            + "pe.id_estudiante, CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_estudiante, "
            + "pe.rol_participacion, pe.posicion_obtenida, pe.observaciones FROM participantes_evento pe "
            + "JOIN eventos ev ON ev.id_evento = pe.id_evento "
            + "JOIN estudiantes e ON e.id_estudiante = pe.id_estudiante ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY ev.fecha_evento";
    private static final String FIND_BY_EVENT = SELECT_COLUMNS + "WHERE pe.id_evento = ? "
            + "ORDER BY pe.posicion_obtenida, e.primer_nombre";
    private static final String INSERT = "INSERT INTO participantes_evento (id_evento, id_estudiante, "
            + "rol_participacion, posicion_obtenida, observaciones) VALUES (?, ?, ?, ?, ?)";

    @Override
    public List<EventParticipant> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapParticipant(resultSet));
    }

    @Override
    public List<EventParticipant> findByEvent(int eventId) {
        return queryForList(FIND_BY_EVENT, resultSet -> mapParticipant(resultSet), eventId);
    }

    @Override
    public int insert(EventParticipant participant) {
        String position = participant.getPosition() == null ? null : participant.getPosition().getDbValue();
        return executeInsert(INSERT, participant.getEventId(), participant.getStudentId(),
                participant.getRole().getDbValue(), position, participant.getNotes());
    }

    private EventParticipant mapParticipant(ResultSet resultSet) throws SQLException {
        String position = resultSet.getString("posicion_obtenida");
        EventParticipant participant = new EventParticipant(resultSet.getInt("id_evento"),
                resultSet.getInt("id_estudiante"), ParticipationRole.fromDbValue(resultSet.getString("rol_participacion")),
                position == null ? null : CompetitionPosition.fromDbValue(position));
        participant.setId(resultSet.getInt("id_participacion"));
        participant.setEventName(resultSet.getString("nombre_evento"));
        participant.setStudentName(resultSet.getString("nombre_estudiante"));
        participant.setNotes(resultSet.getString("observaciones"));
        return participant;
    }
}
