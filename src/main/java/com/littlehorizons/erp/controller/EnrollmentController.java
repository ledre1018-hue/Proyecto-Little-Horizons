package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.EnrollmentDao;
import com.littlehorizons.erp.dao.GradeDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.dao.SubjectDao;
import com.littlehorizons.erp.dao.SubjectRegistrationDao;
import com.littlehorizons.erp.model.AcademicPeriod;
import com.littlehorizons.erp.model.Enrollment;
import com.littlehorizons.erp.model.EnrollmentStatus;
import com.littlehorizons.erp.model.Grade;
import com.littlehorizons.erp.model.Subject;
import com.littlehorizons.erp.model.SubjectRegistration;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.time.LocalDate;
import java.util.List;

/**
 * Business rules for enrollment by grade and registration in subjects.
 */
public class EnrollmentController {

    private final EnrollmentDao enrollmentDao;
    private final GradeDao gradeDao;
    private final SubjectDao subjectDao;
    private final SubjectRegistrationDao registrationDao;
    private final StudentDao studentDao;

    public EnrollmentController(EnrollmentDao enrollmentDao, GradeDao gradeDao, SubjectDao subjectDao,
                                SubjectRegistrationDao registrationDao, StudentDao studentDao) {
        this.enrollmentDao = enrollmentDao;
        this.gradeDao = gradeDao;
        this.subjectDao = subjectDao;
        this.registrationDao = registrationDao;
        this.studentDao = studentDao;
    }

    public List<Grade> listGrades() {
        return gradeDao.findAll();
    }

    public List<Enrollment> getEnrollments(int studentId) {
        requireStudent(studentId);
        return enrollmentDao.findByStudent(studentId);
    }

    /** A student can only have one enrollment per academic year. */
    public int enrollStudent(int studentId, int gradeId, int academicYear, LocalDate enrollmentDate) {
        requireStudent(studentId);
        if (gradeDao.findById(gradeId) == null) {
            throw new BusinessRuleException("El grado seleccionado no existe.");
        }
        boolean alreadyEnrolled = enrollmentDao.findByStudent(studentId).stream()
                .anyMatch(enrollment -> enrollment.getAcademicYear() == academicYear);
        if (alreadyEnrolled) {
            throw new BusinessRuleException("El estudiante ya tiene una matrícula para el año " + academicYear + ".");
        }
        return enrollmentDao.insert(new Enrollment(studentId, gradeId, academicYear, enrollmentDate));
    }

    public void changeEnrollmentStatus(int studentId, int academicYear, EnrollmentStatus newStatus,
                                       LocalDate withdrawalDate) {
        Enrollment enrollment = getEnrollmentOfYear(studentId, academicYear);
        LocalDate dateToSave = newStatus == EnrollmentStatus.WITHDRAWN ? withdrawalDate : null;
        enrollmentDao.updateStatus(enrollment.getId(), newStatus, dateToSave);
    }

    public Enrollment getEnrollmentOfYear(int studentId, int academicYear) {
        List<Enrollment> enrollments = getEnrollments(studentId).stream()
                .filter(enrollment -> enrollment.getAcademicYear() == academicYear)
                .toList();
        if (enrollments.isEmpty()) {
            throw new BusinessRuleException("El estudiante no tiene matrícula en el año " + academicYear + ".");
        }
        return enrollments.get(0);
    }

    /** Subjects of the grade where the student is actively enrolled this year. */
    public List<Subject> getSubjectsForStudent(int studentId, int academicYear) {
        Enrollment enrollment = getEnrollmentOfYear(studentId, academicYear);
        if (!enrollment.isActive()) {
            throw new BusinessRuleException("La matrícula del año " + academicYear + " no está activa.");
        }
        return subjectDao.findAll().stream()
                .filter(subject -> subject.getGradeId() == enrollment.getGradeId())
                .filter(subject -> subject.isActive())
                .toList();
    }

    public int registerInSubject(int studentId, int subjectId, int academicYear, AcademicPeriod period) {
        boolean belongsToGrade = getSubjectsForStudent(studentId, academicYear).stream()
                .anyMatch(subject -> subject.getId() == subjectId);
        if (!belongsToGrade) {
            throw new BusinessRuleException("Solo puede inscribir materias del grado actual del estudiante.");
        }
        boolean alreadyRegistered = registrationDao.findByStudent(studentId).stream()
                .anyMatch(registration -> registration.getSubjectId() == subjectId
                        && registration.getAcademicYear() == academicYear
                        && registration.getPeriod() == period);
        if (alreadyRegistered) {
            throw new BusinessRuleException("El estudiante ya está inscrito en esa materia para el período "
                    + period.getDbValue() + ".");
        }
        return registrationDao.insert(new SubjectRegistration(studentId, subjectId, academicYear, period,
                LocalDate.now()));
    }

    public List<SubjectRegistration> getRegistrations(int studentId) {
        requireStudent(studentId);
        return registrationDao.findByStudent(studentId);
    }

    private void requireStudent(int studentId) {
        if (studentDao.findById(studentId) == null) {
            throw new BusinessRuleException("No existe un estudiante con ID " + studentId + ".");
        }
    }
}
