package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.ClubDao;
import com.littlehorizons.erp.model.Club;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcClubDao extends BaseJdbcDao implements ClubDao {

    private static final String SELECT_COLUMNS = "SELECT c.id_club, c.nombre_club, c.descripcion, "
            + "c.id_profesor_supervisor, CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nombre_profesor, "
            + "c.capacidad_maxima, c.horario_reunion, c.esta_activo FROM clubes c "
            + "LEFT JOIN profesores p ON p.id_profesor = c.id_profesor_supervisor ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY c.nombre_club";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE c.id_club = ?";
    private static final String INSERT = "INSERT INTO clubes (nombre_club, descripcion, id_profesor_supervisor, "
            + "capacidad_maxima, horario_reunion, esta_activo) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE clubes SET nombre_club = ?, descripcion = ?, "
            + "id_profesor_supervisor = ?, capacidad_maxima = ?, horario_reunion = ?, esta_activo = ? "
            + "WHERE id_club = ?";
    private static final String DELETE = "DELETE FROM clubes WHERE id_club = ?";

    @Override
    public List<Club> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapClub(resultSet));
    }

    @Override
    public Club findById(int clubId) {
        return queryForObject(FIND_BY_ID, resultSet -> mapClub(resultSet), clubId);
    }

    @Override
    public int insert(Club club) {
        return executeInsert(INSERT, club.getName(), club.getDescription(), club.getSupervisorId(),
                club.getMaxCapacity(), club.getMeetingSchedule(), club.isActive());
    }

    @Override
    public void update(Club club) {
        executeUpdate(UPDATE, club.getName(), club.getDescription(), club.getSupervisorId(), club.getMaxCapacity(),
                club.getMeetingSchedule(), club.isActive(), club.getId());
    }

    @Override
    public void delete(int clubId) {
        executeUpdate(DELETE, clubId);
    }

    private Club mapClub(ResultSet resultSet) throws SQLException {
        Club club = new Club(resultSet.getString("nombre_club"), resultSet.getString("descripcion"),
                getNullableInt(resultSet, "id_profesor_supervisor"), resultSet.getInt("capacidad_maxima"));
        club.setId(resultSet.getInt("id_club"));
        club.setSupervisorName(resultSet.getString("nombre_profesor"));
        club.setMeetingSchedule(resultSet.getString("horario_reunion"));
        club.setActive(resultSet.getBoolean("esta_activo"));
        return club;
    }
}
