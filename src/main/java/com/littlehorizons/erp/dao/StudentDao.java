package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Student;

import java.util.List;

public interface StudentDao {

    List<Student> findAll();

    Student findById(int studentId);

    Student findByDocument(String documentNumber);

    List<Student> findByGuardian(int guardianId);

    /** Saves the student and links the guardian in one transaction. Returns the new id. */
    int insertWithGuardian(Student student, int guardianId, boolean emergencyContact);

    void update(Student student);

    void delete(int studentId);
}
