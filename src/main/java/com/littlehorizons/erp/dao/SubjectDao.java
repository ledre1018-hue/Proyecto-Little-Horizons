package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Subject;

import java.util.List;

/** Subjects are loaded by the database script, so this DAO only reads. */
public interface SubjectDao {

    List<Subject> findAll();

    Subject findById(int subjectId);
}
