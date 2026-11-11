package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Loan;

import java.time.LocalDate;
import java.util.List;

/**
 * Same contract for student loans and teacher loans (they live in separate tables).
 */
public interface LoanDao<T extends Loan> {

    List<T> findAll();

    T findById(int loanId);

    /** Saves the loan and marks the resource as Prestado in one transaction. */
    int insert(T loan);

    /** Marks the loan as Devuelto and the resource as Disponible in one transaction. */
    void registerReturn(T loan);

    /** Changes Activo loans whose due date already passed to Vencido. Returns how many changed. */
    int markOverdue(LocalDate today);
}
