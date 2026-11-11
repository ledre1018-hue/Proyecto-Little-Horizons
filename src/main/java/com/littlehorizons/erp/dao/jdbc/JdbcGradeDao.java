package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.GradeDao;
import com.littlehorizons.erp.model.Grade;

import java.util.List;

public class JdbcGradeDao extends BaseJdbcDao implements GradeDao {

    private static final String FIND_ALL = "SELECT id_grado, nombre_grado, nivel_numerico FROM grados "
            + "ORDER BY nivel_numerico";
    private static final String FIND_BY_ID = "SELECT id_grado, nombre_grado, nivel_numerico FROM grados "
            + "WHERE id_grado = ?";

    @Override
    public List<Grade> findAll() {
        return queryForList(FIND_ALL, resultSet -> new Grade(resultSet.getInt("id_grado"),
                resultSet.getString("nombre_grado"), resultSet.getInt("nivel_numerico")));
    }

    @Override
    public Grade findById(int gradeId) {
        return queryForObject(FIND_BY_ID, resultSet -> new Grade(resultSet.getInt("id_grado"),
                resultSet.getString("nombre_grado"), resultSet.getInt("nivel_numerico")), gradeId);
    }
}
