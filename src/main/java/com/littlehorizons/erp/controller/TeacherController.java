package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.util.List;

/**
 * Business rules for the teacher directory.
 */
public class TeacherController {

    private final TeacherDao teacherDao;

    public TeacherController(TeacherDao teacherDao) {
        this.teacherDao = teacherDao;
    }

    public List<Teacher> listTeachers() {
        return teacherDao.findAll();
    }

    public List<Teacher> searchBySpecialization(String specialization) {
        return teacherDao.findAll().stream()
                .filter(teacher -> teacher.getSpecialization() != null)
                .filter(teacher -> teacher.getSpecialization().toLowerCase().contains(specialization.toLowerCase()))
                .toList();
    }

    public Teacher findTeacher(int teacherId) {
        Teacher teacher = teacherDao.findById(teacherId);
        if (teacher == null) {
            throw new BusinessRuleException("No existe un profesor con ID " + teacherId + ".");
        }
        return teacher;
    }

    public int registerTeacher(Teacher teacher) {
        boolean documentExists = teacherDao.findAll().stream()
                .anyMatch(saved -> saved.getDocumentNumber().equals(teacher.getDocumentNumber()));
        if (documentExists) {
            throw new BusinessRuleException("Ya existe un profesor con el documento " + teacher.getDocumentNumber() + ".");
        }
        return teacherDao.insert(teacher);
    }

    public void updateTeacher(Teacher teacher) {
        findTeacher(teacher.getId());
        teacherDao.update(teacher);
    }

    public void deleteTeacher(int teacherId) {
        findTeacher(teacherId);
        teacherDao.delete(teacherId);
    }
}
