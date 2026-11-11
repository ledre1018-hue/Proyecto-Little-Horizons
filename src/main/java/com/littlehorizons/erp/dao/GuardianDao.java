package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.StudentGuardian;

import java.util.List;

public interface GuardianDao {

    List<Guardian> findAll();

    Guardian findById(int guardianId);

    Guardian findByDocument(String documentNumber);

    List<StudentGuardian> findByStudent(int studentId);

    int insert(Guardian guardian);

    void update(Guardian guardian);

    void delete(int guardianId);

    void linkToStudent(StudentGuardian link);
}
