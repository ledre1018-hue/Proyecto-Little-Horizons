package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Grade;

import java.util.List;

/** Grades are a fixed catalog, so this DAO only reads. */
public interface GradeDao {

    List<Grade> findAll();

    Grade findById(int gradeId);
}
