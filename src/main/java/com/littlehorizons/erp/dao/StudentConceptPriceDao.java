package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.StudentConceptPrice;

import java.util.List;

public interface StudentConceptPriceDao {

    List<StudentConceptPrice> findAll();

    /** Returns null when the student has no custom price for that concept and year. */
    StudentConceptPrice findByStudentAndConcept(int studentId, int conceptId, int academicYear);

    int insert(StudentConceptPrice price);

    void delete(int priceId);
}
