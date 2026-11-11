package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Subject;

import java.util.List;

public interface SubjectDao {

    List<Subject> findAll();

    Subject findById(int subjectId);

    int insert(Subject subject);

    void update(Subject subject);

    void delete(int subjectId);
}
