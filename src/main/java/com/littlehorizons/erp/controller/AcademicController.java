package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.AcademicRecordDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.dao.SubjectRegistrationDao;
import com.littlehorizons.erp.dao.TeacherAssignmentDao;
import com.littlehorizons.erp.model.AcademicPeriod;
import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.StudentSubjectAverage;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Business rules for grades (notas) and the automatic averages.
 * Averages per subject use periods I to IV; the Final grade is shown in the history only.
 */
public class AcademicController {

    private final AcademicRecordDao recordDao;
    private final SubjectRegistrationDao registrationDao;
    private final TeacherAssignmentDao assignmentDao;
    private final StudentDao studentDao;

    public AcademicController(AcademicRecordDao recordDao, SubjectRegistrationDao registrationDao,
                              TeacherAssignmentDao assignmentDao, StudentDao studentDao) {
        this.recordDao = recordDao;
        this.registrationDao = registrationDao;
        this.assignmentDao = assignmentDao;
        this.studentDao = studentDao;
    }

    public List<TeacherAssignment> getTeachersForSubject(int subjectId, int academicYear) {
        return assignmentDao.findAll().stream()
                .filter(assignment -> assignment.getSubjectId() == subjectId)
                .filter(assignment -> assignment.getAcademicYear() == academicYear)
                .toList();
    }

    /** The student must be registered in the subject and the teacher must have it assigned. */
    public int registerGrade(AcademicRecord record) {
        requireStudent(record.getStudentId());
        boolean isRegistered = registrationDao.findByStudent(record.getStudentId()).stream()
                .anyMatch(registration -> registration.getSubjectId() == record.getSubjectId()
                        && registration.getAcademicYear() == record.getAcademicYear()
                        && (record.getPeriod() == AcademicPeriod.FINAL || registration.getPeriod() == record.getPeriod()));
        if (!isRegistered) {
            throw new BusinessRuleException("El estudiante no está inscrito en esa materia para ese período.");
        }
        boolean teacherHasSubject = assignmentDao.findByTeacher(record.getTeacherId()).stream()
                .anyMatch(assignment -> assignment.getSubjectId() == record.getSubjectId()
                        && assignment.getAcademicYear() == record.getAcademicYear());
        if (!teacherHasSubject) {
            throw new BusinessRuleException("El profesor no tiene asignada esa materia en el año "
                    + record.getAcademicYear() + ".");
        }
        boolean gradeExists = recordDao.findByStudent(record.getStudentId()).stream()
                .anyMatch(saved -> saved.getSubjectId() == record.getSubjectId()
                        && saved.getAcademicYear() == record.getAcademicYear()
                        && saved.getPeriod() == record.getPeriod());
        if (gradeExists) {
            throw new BusinessRuleException("Ya existe una nota para esa materia y período.");
        }
        return recordDao.insert(record);
    }

    /** Complete history since the student joined the school. */
    public List<AcademicRecord> getHistory(int studentId) {
        requireStudent(studentId);
        return recordDao.findByStudent(studentId);
    }

    /** Average of the student in each subject (subject name -> average). */
    public Map<String, Double> getSubjectAverages(int studentId, int academicYear) {
        return getHistory(studentId).stream()
                .filter(record -> record.getAcademicYear() == academicYear)
                .filter(record -> record.getPeriod() != AcademicPeriod.FINAL)
                .collect(Collectors.groupingBy(record -> record.getSubjectName(), () -> new TreeMap<>(),
                        Collectors.averagingDouble(record -> record.getValue())));
    }

    /** General average of the student in one period, across all subjects. */
    public double getPeriodAverage(int studentId, int academicYear, AcademicPeriod period) {
        List<AcademicRecord> periodRecords = getHistory(studentId).stream()
                .filter(record -> record.getAcademicYear() == academicYear && record.getPeriod() == period)
                .toList();
        if (periodRecords.isEmpty()) {
            throw new BusinessRuleException("El estudiante no tiene notas en el período " + period.getDbValue()
                    + " del año " + academicYear + ".");
        }
        return periodRecords.stream().collect(Collectors.averagingDouble(record -> record.getValue()));
    }

    /** Average of the whole course (grade) in each subject. */
    public Map<String, Double> getCourseAverages(int gradeId, int academicYear) {
        return recordDao.findAll().stream()
                .filter(record -> record.getGradeId() == gradeId)
                .filter(record -> record.getAcademicYear() == academicYear)
                .filter(record -> record.getPeriod() != AcademicPeriod.FINAL)
                .collect(Collectors.groupingBy(record -> record.getSubjectName(), () -> new TreeMap<>(),
                        Collectors.averagingDouble(record -> record.getValue())));
    }

    /** Average of every student in every subject of the year. */
    public List<StudentSubjectAverage> calculateAllAverages(int academicYear) {
        Map<String, List<AcademicRecord>> recordsByStudentAndSubject = recordDao.findAll().stream()
                .filter(record -> record.getAcademicYear() == academicYear)
                .filter(record -> record.getPeriod() != AcademicPeriod.FINAL)
                .collect(Collectors.groupingBy(record -> record.getStudentId() + "-" + record.getSubjectId()));
        return recordsByStudentAndSubject.values().stream()
                .map(records -> toAverage(records))
                .toList();
    }

    /** Students with an average below 3.0, ordered by course, subject and average. */
    public List<StudentSubjectAverage> getStudentsNeedingSupport(int academicYear) {
        return calculateAllAverages(academicYear).stream()
                .filter(average -> average.getAverage() < AcademicRecord.PASSING_GRADE)
                .sorted(byCourseAndSubject().thenComparing(average -> average.getAverage()))
                .toList();
    }

    /** Best average of each course and subject. */
    public List<StudentSubjectAverage> getTopAverages(int academicYear) {
        Map<String, List<StudentSubjectAverage>> averagesByCourse = calculateAllAverages(academicYear).stream()
                .collect(Collectors.groupingBy(average -> average.getCourseAndSubject()));
        return averagesByCourse.values().stream()
                .map(courseAverages -> courseAverages.stream()
                        .sorted((first, second) -> Double.compare(second.getAverage(), first.getAverage()))
                        .toList()
                        .get(0))
                .sorted(byCourseAndSubject())
                .toList();
    }

    private Comparator<StudentSubjectAverage> byCourseAndSubject() {
        return Comparator.comparing((StudentSubjectAverage average) -> average.getGradeId())
                .thenComparing(average -> average.getSubjectName());
    }

    private StudentSubjectAverage toAverage(List<AcademicRecord> records) {
        AcademicRecord first = records.get(0);
        double average = records.stream().collect(Collectors.averagingDouble(record -> record.getValue()));
        return new StudentSubjectAverage(first.getStudentId(), first.getStudentName(), first.getSubjectName(),
                first.getGradeId(), first.getGradeName(), average);
    }

    private void requireStudent(int studentId) {
        if (studentDao.findById(studentId) == null) {
            throw new BusinessRuleException("No existe un estudiante con ID " + studentId + ".");
        }
    }
}
