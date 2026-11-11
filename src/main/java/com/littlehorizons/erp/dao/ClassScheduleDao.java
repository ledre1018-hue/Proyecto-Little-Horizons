package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.ClassSchedule;

import java.util.List;

public interface ClassScheduleDao {

    List<ClassSchedule> findByTeacher(int teacherId, int academicYear);

    int insert(ClassSchedule schedule);
}
