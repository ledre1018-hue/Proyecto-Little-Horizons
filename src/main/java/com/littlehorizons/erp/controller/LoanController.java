package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.LoanDao;
import com.littlehorizons.erp.dao.ResourceDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.model.Loan;
import com.littlehorizons.erp.model.LoanStatus;
import com.littlehorizons.erp.model.Resource;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.StudentStatus;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherLoan;
import com.littlehorizons.erp.model.TeacherStatus;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Business rules for loans to students and teachers, returns and overdue loans.
 */
public class LoanController {

    private final LoanDao<StudentLoan> studentLoanDao;
    private final LoanDao<TeacherLoan> teacherLoanDao;
    private final ResourceDao resourceDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;

    public LoanController(LoanDao<StudentLoan> studentLoanDao, LoanDao<TeacherLoan> teacherLoanDao,
                          ResourceDao resourceDao, StudentDao studentDao, TeacherDao teacherDao) {
        this.studentLoanDao = studentLoanDao;
        this.teacherLoanDao = teacherLoanDao;
        this.resourceDao = resourceDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
    }

    public int lendToStudent(StudentLoan loan) {
        if (studentDao.findById(loan.getStudentId()) == null
                || studentDao.findById(loan.getStudentId()).getStatus() != StudentStatus.ACTIVE) {
            throw new BusinessRuleException("El estudiante no existe o no está activo.");
        }
        return registerLoan(studentLoanDao, loan);
    }

    public int lendToTeacher(TeacherLoan loan) {
        Teacher teacher = teacherDao.findById(loan.getTeacherId());
        if (teacher == null || teacher.getStatus() != TeacherStatus.ACTIVE) {
            throw new BusinessRuleException("El profesor no existe o no está activo.");
        }
        return registerLoan(teacherLoanDao, loan);
    }

    public void returnStudentLoan(int loanId, LocalDate returnDate, String notes) {
        registerReturn(studentLoanDao, loanId, returnDate, notes);
    }

    public void returnTeacherLoan(int loanId, LocalDate returnDate, String notes) {
        registerReturn(teacherLoanDao, loanId, returnDate, notes);
    }

    /** Open loans (Activo or Vencido) of students and teachers together. */
    public List<Loan> listOpenLoans() {
        refreshOverdueLoans();
        return allLoans().stream()
                .filter(loan -> loan.isOpen())
                .sorted((first, second) -> first.getDueDate().compareTo(second.getDueDate()))
                .toList();
    }

    public List<StudentLoan> listStudentLoans() {
        return studentLoanDao.findAll();
    }

    public List<TeacherLoan> listTeacherLoans() {
        return teacherLoanDao.findAll();
    }

    /** Active loans whose due date already passed become Vencido. */
    public int refreshOverdueLoans() {
        LocalDate today = LocalDate.now();
        int changed = studentLoanDao.markOverdue(today) + teacherLoanDao.markOverdue(today);
        if (changed > 0) {
            AppLogger.getInstance().info("LoanController.refreshOverdueLoans", changed + " loans marked as overdue");
        }
        return changed;
    }

    public List<Loan> findOverdueLoans() {
        refreshOverdueLoans();
        return allLoans().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.OVERDUE)
                .sorted((first, second) -> first.getDueDate().compareTo(second.getDueDate()))
                .toList();
    }

    /** Works with any kind of loan thanks to the common LoanDao contract. */
    private <T extends Loan> int registerLoan(LoanDao<T> loanDao, T loan) {
        Resource resource = resourceDao.findById(loan.getResourceId());
        if (resource == null) {
            throw new BusinessRuleException("No existe un recurso con ID " + loan.getResourceId() + ".");
        }
        if (!resource.isAvailable()) {
            throw new BusinessRuleException("El recurso no está disponible. Estado actual: "
                    + resource.getStatus().getLabel() + ".");
        }
        if (!loan.getDueDate().isAfter(loan.getLoanDate())) {
            throw new BusinessRuleException("La fecha de devolución debe ser posterior a la fecha de préstamo.");
        }
        return loanDao.insert(loan);
    }

    private <T extends Loan> void registerReturn(LoanDao<T> loanDao, int loanId, LocalDate returnDate,
                                                 String notes) {
        T loan = loanDao.findById(loanId);
        if (loan == null) {
            throw new BusinessRuleException("No existe un préstamo con ID " + loanId + ".");
        }
        if (!loan.isOpen()) {
            throw new BusinessRuleException("Ese préstamo ya fue devuelto.");
        }
        if (returnDate.isBefore(loan.getLoanDate())) {
            throw new BusinessRuleException("La fecha de devolución no puede ser anterior al préstamo ("
                    + loan.getLoanDate() + ").");
        }
        loan.setReturnDate(returnDate);
        loan.setNotes(notes);
        loanDao.registerReturn(loan);
    }

    private List<Loan> allLoans() {
        List<Loan> loans = new ArrayList<>();
        loans.addAll(studentLoanDao.findAll());
        loans.addAll(teacherLoanDao.findAll());
        return loans;
    }
}
