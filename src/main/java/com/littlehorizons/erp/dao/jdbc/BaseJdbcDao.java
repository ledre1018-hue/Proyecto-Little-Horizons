package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.DataAccessException;
import com.littlehorizons.erp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared JDBC steps for every DAO: open the connection, bind the ? parameters,
 * run the PreparedStatement and close everything with try-with-resources.
 */
public abstract class BaseJdbcDao {

    protected Connection openConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    protected <T> List<T> queryForList(String sql, RowMapper<T> mapper, Object... parameters) {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParameters(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(mapper.mapRow(resultSet));
                }
                return results;
            }
        } catch (SQLException e) {
            throw handleError(getClass().getSimpleName() + ".queryForList", e);
        }
    }

    /** Returns the first row, or null when the query finds nothing. */
    protected <T> T queryForObject(String sql, RowMapper<T> mapper, Object... parameters) {
        List<T> results = queryForList(sql, mapper, parameters);
        return results.isEmpty() ? null : results.get(0);
    }

    protected int executeUpdate(String sql, Object... parameters) {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParameters(statement, parameters);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw handleError(getClass().getSimpleName() + ".executeUpdate", e);
        }
    }

    protected int executeInsert(String sql, Object... parameters) {
        try (Connection connection = openConnection()) {
            return insertAndGetId(connection, sql, parameters);
        } catch (SQLException e) {
            throw handleError(getClass().getSimpleName() + ".executeInsert", e);
        }
    }

    /** Insert inside an existing connection (used by transactions). Returns the generated id. */
    protected int insertAndGetId(Connection connection, String sql, Object... parameters) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindParameters(statement, parameters);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("The database did not return the generated id.");
            }
        }
    }

    /** Update inside an existing connection (used by transactions). */
    protected int updateInTransaction(Connection connection, String sql, Object... parameters)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParameters(statement, parameters);
            return statement.executeUpdate();
        }
    }

    protected void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int index = 0; index < parameters.length; index++) {
            statement.setObject(index + 1, parameters[index]);
        }
    }

    protected void rollbackQuietly(Connection connection, String source) {
        try {
            connection.rollback();
            AppLogger.getInstance().warning(source, "Transaction rolled back");
        } catch (SQLException e) {
            AppLogger.getInstance().error(source, "Rollback failed", e);
        }
    }

    protected DataAccessException handleError(String source, SQLException exception) {
        AppLogger.getInstance().error(source, "SQL error " + exception.getErrorCode() + ": "
                + exception.getMessage(), exception);
        return new DataAccessException(SqlErrorTranslator.toUserMessage(exception), exception);
    }

    protected static LocalDate getDate(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getObject(column, LocalDate.class);
    }

    protected static Integer getNullableInt(ResultSet resultSet, String column) throws SQLException {
        int value = resultSet.getInt(column);
        return resultSet.wasNull() ? null : value;
    }
}
