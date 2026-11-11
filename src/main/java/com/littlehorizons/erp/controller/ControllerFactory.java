package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.controller.report.ClubParticipationReport;
import com.littlehorizons.erp.controller.report.ConcerningAbsencesReport;
import com.littlehorizons.erp.controller.report.DelinquentStudentsReport;
import com.littlehorizons.erp.controller.report.EventParticipationReport;
import com.littlehorizons.erp.controller.report.OverdueLoansReport;
import com.littlehorizons.erp.controller.report.PaymentsByConceptReport;
import com.littlehorizons.erp.controller.report.PaymentsByMethodReport;
import com.littlehorizons.erp.controller.report.Report;
import com.littlehorizons.erp.controller.report.StudentsNeedingSupportReport;
import com.littlehorizons.erp.controller.report.TopAveragesReport;
import com.littlehorizons.erp.controller.tax.TaxStrategyFactory;
import com.littlehorizons.erp.dao.DaoFactory;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.LogFileReader;
import com.littlehorizons.erp.util.PdfInvoiceGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates every controller once and injects its DAOs through the constructor.
 * Views only receive this factory, so they never see a DAO.
 */
public class ControllerFactory {

    private final StudentController studentController;
    private final GuardianController guardianController;
    private final EnrollmentController enrollmentController;
    private final AcademicController academicController;
    private final AttendanceController attendanceController;
    private final TeacherController teacherController;
    private final AssignmentController assignmentController;
    private final ClubController clubController;
    private final EventController eventController;
    private final PaymentConceptController paymentConceptController;
    private final PaymentController paymentController;
    private final ResourceController resourceController;
    private final LoanController loanController;
    private final ReportController reportController;

    public ControllerFactory(DaoFactory daos) {
        studentController = new StudentController(daos.createStudentDao(), daos.createGuardianDao(),
                daos.createPaymentDao());
        guardianController = new GuardianController(daos.createGuardianDao(), daos.createStudentDao());
        enrollmentController = new EnrollmentController(daos.createEnrollmentDao(), daos.createGradeDao(),
                daos.createSubjectDao(), daos.createSubjectRegistrationDao(), daos.createStudentDao());
        academicController = new AcademicController(daos.createAcademicRecordDao(),
                daos.createSubjectRegistrationDao(), daos.createTeacherAssignmentDao(), daos.createStudentDao());
        attendanceController = new AttendanceController(daos.createAttendanceDao(),
                daos.createSubjectRegistrationDao(), daos.createStudentDao());
        teacherController = new TeacherController(daos.createTeacherDao());
        assignmentController = new AssignmentController(daos.createTeacherAssignmentDao(),
                daos.createClassScheduleDao(), daos.createTeacherDao(), daos.createSubjectDao());
        clubController = new ClubController(daos.createClubDao(), daos.createClubMembershipDao(),
                daos.createStudentDao(), daos.createTeacherDao());
        eventController = new EventController(daos.createEventDao(), daos.createEventParticipantDao(),
                daos.createStudentDao());
        paymentConceptController = new PaymentConceptController(daos.createPaymentConceptDao(),
                daos.createStudentConceptPriceDao(), daos.createStudentDao());
        paymentController = new PaymentController(daos.createPaymentDao(), daos.createInvoiceDao(),
                daos.createStudentDao(), daos.createGuardianDao(), daos.createPaymentConceptDao(),
                new TaxStrategyFactory(), new PdfInvoiceGenerator());
        resourceController = new ResourceController(daos.createResourceDao(), daos.createResourceCategoryDao());
        loanController = new LoanController(daos.createStudentLoanDao(), daos.createTeacherLoanDao(),
                daos.createResourceDao(), daos.createStudentDao(), daos.createTeacherDao());
        reportController = new ReportController(buildReports(),
                new LogFileReader(AppLogger.getInstance().getLogFile()));
    }

    private List<Report> buildReports() {
        List<Report> reports = new ArrayList<>();
        reports.add(new ConcerningAbsencesReport(attendanceController));
        reports.add(new DelinquentStudentsReport(paymentController));
        reports.add(new OverdueLoansReport(loanController));
        reports.add(new StudentsNeedingSupportReport(academicController));
        reports.add(new TopAveragesReport(academicController));
        reports.add(new EventParticipationReport(eventController));
        reports.add(new ClubParticipationReport(clubController));
        reports.add(new PaymentsByMethodReport(paymentController));
        reports.add(new PaymentsByConceptReport(paymentController));
        return reports;
    }

    public StudentController getStudentController() {
        return studentController;
    }

    public GuardianController getGuardianController() {
        return guardianController;
    }

    public EnrollmentController getEnrollmentController() {
        return enrollmentController;
    }

    public AcademicController getAcademicController() {
        return academicController;
    }

    public AttendanceController getAttendanceController() {
        return attendanceController;
    }

    public TeacherController getTeacherController() {
        return teacherController;
    }

    public AssignmentController getAssignmentController() {
        return assignmentController;
    }

    public ClubController getClubController() {
        return clubController;
    }

    public EventController getEventController() {
        return eventController;
    }

    public PaymentConceptController getPaymentConceptController() {
        return paymentConceptController;
    }

    public PaymentController getPaymentController() {
        return paymentController;
    }

    public ResourceController getResourceController() {
        return resourceController;
    }

    public LoanController getLoanController() {
        return loanController;
    }

    public ReportController getReportController() {
        return reportController;
    }
}
