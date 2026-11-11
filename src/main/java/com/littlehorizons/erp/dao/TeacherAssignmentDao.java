package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.TeacherAssignment;

import java.util.List;

public interface TeacherAssignmentDao {

    List<TeacherAssignment> findAll();

    List<TeacherAssignment> findByTeacher(int teacherId);

    TeacherAssignment findById(int assignmentId);

    int insert(TeacherAssignment assignment);
}
