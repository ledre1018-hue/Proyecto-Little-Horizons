package com.littlehorizons.erp.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Converts the current row of a ResultSet into an object.
 * It is our own functional interface because java.util.function.Function cannot throw SQLException.
 */
@FunctionalInterface
public interface RowMapper<T> {

    T mapRow(ResultSet resultSet) throws SQLException;
}
