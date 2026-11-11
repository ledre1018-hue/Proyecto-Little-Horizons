package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.dao.jdbc.JdbcAcademicRecordDao;
import com.littlehorizons.erp.dao.jdbc.JdbcAttendanceDao;
import com.littlehorizons.erp.dao.jdbc.JdbcClassScheduleDao;
import com.littlehorizons.erp.dao.jdbc.JdbcClubDao;
import com.littlehorizons.erp.dao.jdbc.JdbcClubMembershipDao;
import com.littlehorizons.erp.dao.jdbc.JdbcEnrollmentDao;
import com.littlehorizons.erp.dao.jdbc.JdbcEventDao;
import com.littlehorizons.erp.dao.jdbc.JdbcEventParticipantDao;
import com.littlehorizons.erp.dao.jdbc.JdbcGradeDao;
import com.littlehorizons.erp.dao.jdbc.JdbcGuardianDao;
import com.littlehorizons.erp.dao.jdbc.JdbcInvoiceDao;
import com.littlehorizons.erp.dao.jdbc.JdbcPaymentConceptDao;
import com.littlehorizons.erp.dao.jdbc.JdbcPaymentDao;
import com.littlehorizons.erp.dao.jdbc.JdbcResourceCategoryDao;
import com.littlehorizons.erp.dao.jdbc.JdbcResourceDao;
import com.littlehorizons.erp.dao.jdbc.JdbcStudentConceptPriceDao;
import com.littlehorizons.erp.dao.jdbc.JdbcStudentDao;
import com.littlehorizons.erp.dao.jdbc.JdbcStudentLoanDao;
import com.littlehorizons.erp.dao.jdbc.JdbcSubjectDao;
import com.littlehorizons.erp.dao.jdbc.JdbcSubjectRegistrationDao;
import com.littlehorizons.erp.dao.jdbc.JdbcTeacherAssignmentDao;
import com.littlehorizons.erp.dao.jdbc.JdbcTeacherDao;
import com.littlehorizons.erp.dao.jdbc.JdbcTeacherLoanDao;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;

/**
 * Factory pattern: the only place that knows the JDBC implementations.
 * The rest of the program works with the DAO interfaces.
 */
public class DaoFactory {

    public StudentDao createStudentDao() {
        return new JdbcStudentDao();
    }

    public GuardianDao createGuardianDao() {
        return new JdbcGuardianDao();
    }

    public GradeDao createGradeDao() {
        return new JdbcGradeDao();
    }

    public EnrollmentDao createEnrollmentDao() {
        return new JdbcEnrollmentDao();
    }

    public SubjectDao createSubjectDao() {
        return new JdbcSubjectDao();
    }

    public SubjectRegistrationDao createSubjectRegistrationDao() {
        return new JdbcSubjectRegistrationDao();
    }

    public AcademicRecordDao createAcademicRecordDao() {
        return new JdbcAcademicRecordDao();
    }

    public AttendanceDao createAttendanceDao() {
        return new JdbcAttendanceDao();
    }

    public TeacherDao createTeacherDao() {
        return new JdbcTeacherDao();
    }

    public TeacherAssignmentDao createTeacherAssignmentDao() {
        return new JdbcTeacherAssignmentDao();
    }

    public ClassScheduleDao createClassScheduleDao() {
        return new JdbcClassScheduleDao();
    }

    public ClubDao createClubDao() {
        return new JdbcClubDao();
    }

    public ClubMembershipDao createClubMembershipDao() {
        return new JdbcClubMembershipDao();
    }

    public EventDao createEventDao() {
        return new JdbcEventDao();
    }

    public EventParticipantDao createEventParticipantDao() {
        return new JdbcEventParticipantDao();
    }

    public PaymentConceptDao createPaymentConceptDao() {
        return new JdbcPaymentConceptDao();
    }

    public StudentConceptPriceDao createStudentConceptPriceDao() {
        return new JdbcStudentConceptPriceDao();
    }

    public PaymentDao createPaymentDao() {
        return new JdbcPaymentDao();
    }

    public InvoiceDao createInvoiceDao() {
        return new JdbcInvoiceDao();
    }

    public ResourceDao createResourceDao() {
        return new JdbcResourceDao();
    }

    public ResourceCategoryDao createResourceCategoryDao() {
        return new JdbcResourceCategoryDao();
    }

    public LoanDao<StudentLoan> createStudentLoanDao() {
        return new JdbcStudentLoanDao();
    }

    public LoanDao<TeacherLoan> createTeacherLoanDao() {
        return new JdbcTeacherLoanDao();
    }
}
