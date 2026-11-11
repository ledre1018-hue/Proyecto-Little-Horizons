package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.SubjectRegistration;

import java.util.List;

public interface SubjectRegistrationDao {

    List<SubjectRegistration> findByStudent(int studentId);

    int insert(SubjectRegistration registration);
}
