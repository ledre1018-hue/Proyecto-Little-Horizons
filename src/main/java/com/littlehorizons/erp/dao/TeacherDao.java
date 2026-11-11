package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Teacher;

import java.util.List;

public interface TeacherDao {

    List<Teacher> findAll();

    Teacher findById(int teacherId);

    int insert(Teacher teacher);

    void update(Teacher teacher);

    void delete(int teacherId);
}
