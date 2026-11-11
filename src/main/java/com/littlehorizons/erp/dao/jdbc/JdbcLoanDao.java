package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.LoanDao;
import com.littlehorizons.erp.model.Loan;
import com.littlehorizons.erp.model.LoanStatus;
import com.littlehorizons.erp.model.ResourceStatus;
import com.littlehorizons.erp.util.DataAccessException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Transaction steps shared by student and teacher loans. Each subclass only gives its own
 * SQL (its table) and how to read one row.
 */
public abstract class JdbcLoanDao<T extends Loan> extends BaseJdbcDao implements LoanDao<T> {

    private static final String RESERVE_RESOURCE = "UPDATE recursos SET estado_recurso = ? "
            + "WHERE id_recurso = ? AND estado_recurso = ?";
    private static final String RELEASE_RESOURCE = "UPDATE recursos SET estado_recurso = ? WHERE id_recurso = ?";

    protected abstract String findAllSql();

    protected abstract String findByIdSql();

    protected abstract String insertSql();

    protected abstract String returnSql();

    protected abstract String markOverdueSql();

    protected abstract T mapLoan(ResultSet resultSet) throws SQLException;

    @Override
    public List<T> findAll() {
        return queryForList(findAllSql(), resultSet -> mapLoan(resultSet));
    }

    @Override
    public T findById(int loanId) {
        return queryForObject(findByIdSql(), resultSet -> mapLoan(resultSet), loanId);
    }

    @Override
    public int insert(T loan) {
        String source = getClass().getSimpleName() + ".insert";
        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                int reserved = updateInTransaction(connection, RESERVE_RESOURCE, ResourceStatus.BORROWED.getDbValue(),
                        loan.getResourceId(), ResourceStatus.AVAILABLE.getDbValue());
                if (reserved == 0) {
                    connection.rollback();
                    throw new DataAccessException("El recurso ya no está disponible para préstamo.");
                }
                int loanId = insertAndGetId(connection, insertSql(), loan.getResourceId(), loan.getBorrowerId(),
                        loan.getLoanDate(), loan.getDueDate(), loan.getStatus().getDbValue(), loan.getNotes());
                connection.commit();
                return loanId;
            } catch (SQLException e) {
                rollbackQuietly(connection, source);
                throw e;
            }
        } catch (SQLException e) {
            throw handleError(source, e);
        }
    }

    @Override
    public void registerReturn(T loan) {
        String source = getClass().getSimpleName() + ".registerReturn";
        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                updateInTransaction(connection, returnSql(), LoanStatus.RETURNED.getDbValue(), loan.getReturnDate(),
                        loan.getNotes(), loan.getId());
                updateInTransaction(connection, RELEASE_RESOURCE, ResourceStatus.AVAILABLE.getDbValue(),
                        loan.getResourceId());
                connection.commit();
            } catch (SQLException e) {
                rollbackQuietly(connection, source);
                throw e;
            }
        } catch (SQLException e) {
            throw handleError(source, e);
        }
    }

    @Override
    public int markOverdue(LocalDate today) {
        return executeUpdate(markOverdueSql(), LoanStatus.OVERDUE.getDbValue(), LoanStatus.ACTIVE.getDbValue(), today);
    }

    /** Reads the columns that both loan tables have in common. */
    protected void mapCommonColumns(ResultSet resultSet, T loan) throws SQLException {
        loan.setId(resultSet.getInt("id_prestamo"));
        loan.setResourceId(resultSet.getInt("id_recurso"));
        loan.setResourceName(resultSet.getString("nombre_recurso"));
        loan.setBorrowerName(resultSet.getString("nombre_prestatario"));
        loan.setLoanDate(getDate(resultSet, "fecha_prestamo"));
        loan.setDueDate(getDate(resultSet, "fecha_devolucion_esperada"));
        loan.setReturnDate(getDate(resultSet, "fecha_devolucion_real"));
        loan.setStatus(LoanStatus.fromDbValue(resultSet.getString("estado_prestamo")));
        loan.setNotes(resultSet.getString("observaciones"));
    }
}
