package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Loan of a resource to a student (table prestamos_estudiante).
 */
public class StudentLoan extends Loan {

    private int studentId;

    public StudentLoan() {
    }

    public StudentLoan(int resourceId, int studentId, LocalDate loanDate, LocalDate dueDate) {
        super(resourceId, loanDate, dueDate);
        this.studentId = studentId;
    }

    @Override
    public int getBorrowerId() {
        return studentId;
    }

    @Override
    public String getBorrowerType() {
        return "Estudiante";
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
}
