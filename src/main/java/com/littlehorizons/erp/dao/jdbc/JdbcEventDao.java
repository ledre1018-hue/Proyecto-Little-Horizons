package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.EventDao;
import com.littlehorizons.erp.model.EventType;
import com.littlehorizons.erp.model.SchoolEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcEventDao extends BaseJdbcDao implements EventDao {

    private static final String SELECT_COLUMNS = "SELECT id_evento, nombre_evento, descripcion, tipo_evento, "
            + "fecha_evento, lugar, tiene_competencia, id_organizador, esta_activo FROM eventos ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY fecha_evento";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE id_evento = ?";
    private static final String INSERT = "INSERT INTO eventos (nombre_evento, descripcion, tipo_evento, "
            + "fecha_evento, lugar, tiene_competencia, id_organizador, esta_activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE eventos SET nombre_evento = ?, descripcion = ?, tipo_evento = ?, "
            + "fecha_evento = ?, lugar = ?, tiene_competencia = ?, id_organizador = ?, esta_activo = ? "
            + "WHERE id_evento = ?";
    private static final String DELETE = "DELETE FROM eventos WHERE id_evento = ?";

    @Override
    public List<SchoolEvent> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapEvent(resultSet));
    }

    @Override
    public SchoolEvent findById(int eventId) {
        return queryForObject(FIND_BY_ID, resultSet -> mapEvent(resultSet), eventId);
    }

    @Override
    public int insert(SchoolEvent event) {
        return executeInsert(INSERT, event.getName(), event.getDescription(), event.getType().getDbValue(),
                event.getEventDate(), event.getPlace(), event.hasCompetition(), event.getOrganizerId(),
                event.isActive());
    }

    @Override
    public void update(SchoolEvent event) {
        executeUpdate(UPDATE, event.getName(), event.getDescription(), event.getType().getDbValue(),
                event.getEventDate(), event.getPlace(), event.hasCompetition(), event.getOrganizerId(),
                event.isActive(), event.getId());
    }

    @Override
    public void delete(int eventId) {
        executeUpdate(DELETE, eventId);
    }

    private SchoolEvent mapEvent(ResultSet resultSet) throws SQLException {
        SchoolEvent event = new SchoolEvent(resultSet.getString("nombre_evento"),
                EventType.fromDbValue(resultSet.getString("tipo_evento")), getDate(resultSet, "fecha_evento"),
                resultSet.getBoolean("tiene_competencia"));
        event.setId(resultSet.getInt("id_evento"));
        event.setDescription(resultSet.getString("descripcion"));
        event.setPlace(resultSet.getString("lugar"));
        event.setOrganizerId(getNullableInt(resultSet, "id_organizador"));
        event.setActive(resultSet.getBoolean("esta_activo"));
        return event;
    }
}
