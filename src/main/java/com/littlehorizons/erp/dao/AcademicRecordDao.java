package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.AcademicRecord;

import java.util.List;

public interface AcademicRecordDao {

    List<AcademicRecord> findAll();

    List<AcademicRecord> findByStudent(int studentId);

    int insert(AcademicRecord record);
}
