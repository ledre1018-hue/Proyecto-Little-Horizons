package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Common data of a resource loan. StudentLoan and TeacherLoan add who borrows it.
 */
public abstract class Loan {

    private int id;
    private int resourceId;
    private String resourceName;
    private String borrowerName;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private String notes;

    protected Loan() {
        this.status = LoanStatus.ACTIVE;
    }

    protected Loan(int resourceId, LocalDate loanDate, LocalDate dueDate) {
        this.resourceId = resourceId;
        setLoanDate(loanDate);
        setDueDate(dueDate);
        this.status = LoanStatus.ACTIVE;
    }

    public abstract int getBorrowerId();

    /** Borrower type shown on screen: "Estudiante" or "Profesor". */
    public abstract String getBorrowerType();

    public boolean isOpen() {
        return status != LoanStatus.RETURNED;
    }

    public boolean isPastDue(LocalDate today) {
        return isOpen() && dueDate.isBefore(today);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        if (loanDate == null) {
            throw new IllegalArgumentException("La fecha de préstamo es obligatoria.");
        }
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        if (dueDate == null || (loanDate != null && !dueDate.isAfter(loanDate))) {
            throw new IllegalArgumentException("La fecha de devolución debe ser posterior a la fecha de préstamo.");
        }
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        if (returnDate != null && returnDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("La fecha de devolución no puede ser anterior al préstamo.");
        }
        this.returnDate = returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
