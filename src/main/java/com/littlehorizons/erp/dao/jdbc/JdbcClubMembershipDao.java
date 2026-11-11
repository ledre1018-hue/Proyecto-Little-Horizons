package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.ClubMembershipDao;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.MembershipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JdbcClubMembershipDao extends BaseJdbcDao implements ClubMembershipDao {

    private static final String SELECT_COLUMNS = "SELECT mc.id_membresia, mc.id_estudiante, "
            + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_estudiante, mc.id_club, c.nombre_club, "
            + "mc.fecha_ingreso, mc.estado_membresia, mc.fecha_retiro, mc.motivo_retiro FROM membresias_club mc "
            + "JOIN estudiantes e ON e.id_estudiante = mc.id_estudiante JOIN clubes c ON c.id_club = mc.id_club ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY c.nombre_club, e.primer_nombre";
    private static final String FIND_BY_CLUB = SELECT_COLUMNS + "WHERE mc.id_club = ? "
            + "ORDER BY mc.estado_membresia, e.primer_nombre";
    private static final String INSERT = "INSERT INTO membresias_club (id_estudiante, id_club, fecha_ingreso, "
            + "estado_membresia) VALUES (?, ?, ?, ?)";
    private static final String WITHDRAW = "UPDATE membresias_club SET estado_membresia = ?, fecha_retiro = ?, "
            + "motivo_retiro = ? WHERE id_membresia = ?";

    @Override
    public List<ClubMembership> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapMembership(resultSet));
    }

    @Override
    public List<ClubMembership> findByClub(int clubId) {
        return queryForList(FIND_BY_CLUB, resultSet -> mapMembership(resultSet), clubId);
    }

    @Override
    public int insert(ClubMembership membership) {
        return executeInsert(INSERT, membership.getStudentId(), membership.getClubId(), membership.getJoinDate(),
                membership.getStatus().getDbValue());
    }

    @Override
    public void withdraw(int membershipId, LocalDate withdrawalDate, String reason) {
        executeUpdate(WITHDRAW, MembershipStatus.WITHDRAWN.getDbValue(), withdrawalDate, reason, membershipId);
    }

    private ClubMembership mapMembership(ResultSet resultSet) throws SQLException {
        ClubMembership membership = new ClubMembership(resultSet.getInt("id_estudiante"),
                resultSet.getInt("id_club"), getDate(resultSet, "fecha_ingreso"));
        membership.setId(resultSet.getInt("id_membresia"));
        membership.setStudentName(resultSet.getString("nombre_estudiante"));
        membership.setClubName(resultSet.getString("nombre_club"));
        membership.setStatus(MembershipStatus.fromDbValue(resultSet.getString("estado_membresia")));
        membership.setWithdrawalDate(getDate(resultSet, "fecha_retiro"));
        membership.setWithdrawalReason(resultSet.getString("motivo_retiro"));
        return membership;
    }
}
