package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Attendance;

import java.util.List;

public interface AttendanceDao {

    List<Attendance> findAll();

    List<Attendance> findByStudent(int studentId);

    int insert(Attendance attendance);
}
