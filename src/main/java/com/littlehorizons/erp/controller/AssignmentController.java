package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.ClassScheduleDao;
import com.littlehorizons.erp.dao.SubjectDao;
import com.littlehorizons.erp.dao.TeacherAssignmentDao;
import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.model.ClassSchedule;
import com.littlehorizons.erp.model.Subject;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.model.TeacherStatus;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.util.List;

/**
 * Business rules for assigning subjects to teachers and their class schedule.
 */
public class AssignmentController {

    private final TeacherAssignmentDao assignmentDao;
    private final ClassScheduleDao scheduleDao;
    private final TeacherDao teacherDao;
    private final SubjectDao subjectDao;

    public AssignmentController(TeacherAssignmentDao assignmentDao, ClassScheduleDao scheduleDao,
                                TeacherDao teacherDao, SubjectDao subjectDao) {
        this.assignmentDao = assignmentDao;
        this.scheduleDao = scheduleDao;
        this.teacherDao = teacherDao;
        this.subjectDao = subjectDao;
    }

    public List<Subject> listSubjects() {
        return subjectDao.findAll();
    }

    public List<TeacherAssignment> getAssignments(int teacherId) {
        requireTeacher(teacherId);
        return assignmentDao.findByTeacher(teacherId);
    }

    public int assignSubject(TeacherAssignment assignment) {
        Teacher teacher = requireTeacher(assignment.getTeacherId());
        if (teacher.getStatus() != TeacherStatus.ACTIVE) {
            throw new BusinessRuleException("Solo se pueden asignar materias a profesores activos.");
        }
        if (subjectDao.findById(assignment.getSubjectId()) == null) {
            throw new BusinessRuleException("La materia seleccionada no existe.");
        }
        boolean alreadyAssigned = assignmentDao.findByTeacher(assignment.getTeacherId()).stream()
                .anyMatch(saved -> saved.getSubjectId() == assignment.getSubjectId()
                        && saved.getAcademicYear() == assignment.getAcademicYear()
                        && saved.getSection().equals(assignment.getSection()));
        if (alreadyAssigned) {
            throw new BusinessRuleException("El profesor ya tiene esa materia en esa sección y año.");
        }
        return assignmentDao.insert(assignment);
    }

    /** Rejects the schedule if the same teacher already has a class at that day and time. */
    public int addSchedule(ClassSchedule schedule) {
        TeacherAssignment assignment = assignmentDao.findById(schedule.getAssignmentId());
        if (assignment == null) {
            throw new BusinessRuleException("La asignación seleccionada no existe.");
        }
        List<ClassSchedule> conflicts = scheduleDao.findByTeacher(assignment.getTeacherId(),
                        assignment.getAcademicYear()).stream()
                .filter(saved -> saved.overlapsWith(schedule))
                .toList();
        if (!conflicts.isEmpty()) {
            ClassSchedule conflict = conflicts.get(0);
            throw new BusinessRuleException("Choque de horario: el profesor ya dicta " + conflict.getSubjectName()
                    + " el " + conflict.getDay().getLabel() + " de " + conflict.getStartTime() + " a "
                    + conflict.getEndTime() + ".");
        }
        return scheduleDao.insert(schedule);
    }

    public List<ClassSchedule> getSchedule(int teacherId, int academicYear) {
        requireTeacher(teacherId);
        return scheduleDao.findByTeacher(teacherId, academicYear);
    }

    private Teacher requireTeacher(int teacherId) {
        Teacher teacher = teacherDao.findById(teacherId);
        if (teacher == null) {
            throw new BusinessRuleException("No existe un profesor con ID " + teacherId + ".");
        }
        return teacher;
    }
}
