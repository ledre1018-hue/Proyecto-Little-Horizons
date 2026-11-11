package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.AttendanceDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.dao.SubjectRegistrationDao;
import com.littlehorizons.erp.model.AbsenceAlert;
import com.littlehorizons.erp.model.Attendance;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Business rules for daily attendance and the absence report.
 */
public class AttendanceController {

    public static final int MAX_WEEKLY_ABSENCES = 3;

    private final AttendanceDao attendanceDao;
    private final SubjectRegistrationDao registrationDao;
    private final StudentDao studentDao;

    public AttendanceController(AttendanceDao attendanceDao, SubjectRegistrationDao registrationDao,
                                StudentDao studentDao) {
        this.attendanceDao = attendanceDao;
        this.registrationDao = registrationDao;
        this.studentDao = studentDao;
    }

    /** One record per student, subject and date, only for subjects the student is registered in. */
    public int registerAttendance(Attendance attendance) {
        if (studentDao.findById(attendance.getStudentId()) == null) {
            throw new BusinessRuleException("No existe un estudiante con ID " + attendance.getStudentId() + ".");
        }
        int year = attendance.getDate().getYear();
        boolean isRegistered = registrationDao.findByStudent(attendance.getStudentId()).stream()
                .anyMatch(registration -> registration.getSubjectId() == attendance.getSubjectId()
                        && registration.getAcademicYear() == year);
        if (!isRegistered) {
            throw new BusinessRuleException("El estudiante no está inscrito en esa materia en el año " + year + ".");
        }
        boolean alreadyRecorded = attendanceDao.findByStudent(attendance.getStudentId()).stream()
                .anyMatch(saved -> saved.getSubjectId() == attendance.getSubjectId()
                        && saved.getDate().equals(attendance.getDate()));
        if (alreadyRecorded) {
            throw new BusinessRuleException("Ya existe un registro de asistencia para esa materia y fecha.");
        }
        return attendanceDao.insert(attendance);
    }

    public List<Attendance> getAttendance(int studentId) {
        if (studentDao.findById(studentId) == null) {
            throw new BusinessRuleException("No existe un estudiante con ID " + studentId + ".");
        }
        return attendanceDao.findByStudent(studentId);
    }

    /** Students with more than MAX_WEEKLY_ABSENCES absences in the same week (Monday to Sunday). */
    public List<AbsenceAlert> findConcerningAbsences() {
        Map<Integer, Map<LocalDate, Long>> absencesByStudentAndWeek = attendanceDao.findAll().stream()
                .filter(attendance -> attendance.isAbsence())
                .collect(Collectors.groupingBy(attendance -> attendance.getStudentId(),
                        Collectors.groupingBy(attendance -> attendance.getWeekStart(), Collectors.counting())));
        Map<Integer, String> studentNames = new HashMap<>();
        for (Student student : studentDao.findAll()) {
            studentNames.put(student.getId(), student.getFullName());
        }
        return absencesByStudentAndWeek.entrySet().stream()
                .flatMap(studentEntry -> studentEntry.getValue().entrySet().stream()
                        .filter(weekEntry -> weekEntry.getValue() > MAX_WEEKLY_ABSENCES)
                        .map(weekEntry -> new AbsenceAlert(studentEntry.getKey(),
                                studentNames.getOrDefault(studentEntry.getKey(), "Desconocido"),
                                weekEntry.getKey(), weekEntry.getValue())))
                .sorted((first, second) -> Long.compare(second.getAbsences(), first.getAbsences()))
                .toList();
    }
}
