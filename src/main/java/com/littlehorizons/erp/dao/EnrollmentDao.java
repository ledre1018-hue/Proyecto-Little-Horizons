package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Enrollment;
import com.littlehorizons.erp.model.EnrollmentStatus;

import java.time.LocalDate;
import java.util.List;

public interface EnrollmentDao {

    List<Enrollment> findByStudent(int studentId);

    List<Enrollment> findByYear(int academicYear);

    int insert(Enrollment enrollment);

    void updateStatus(int enrollmentId, EnrollmentStatus status, LocalDate withdrawalDate);
}
