package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Loan of a resource to a teacher (table prestamos_profesor).
 */
public class TeacherLoan extends Loan {

    private int teacherId;

    public TeacherLoan() {
    }

    public TeacherLoan(int resourceId, int teacherId, LocalDate loanDate, LocalDate dueDate) {
        super(resourceId, loanDate, dueDate);
        this.teacherId = teacherId;
    }

    @Override
    public int getBorrowerId() {
        return teacherId;
    }

    @Override
    public String getBorrowerType() {
        return "Profesor";
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
}
