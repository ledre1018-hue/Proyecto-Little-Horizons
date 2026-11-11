package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.GuardianDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.util.List;

/**
 * Business rules for guardians (acudientes).
 */
public class GuardianController {

    private final GuardianDao guardianDao;
    private final StudentDao studentDao;

    public GuardianController(GuardianDao guardianDao, StudentDao studentDao) {
        this.guardianDao = guardianDao;
        this.studentDao = studentDao;
    }

    public List<Guardian> listGuardians() {
        return guardianDao.findAll();
    }

    public Guardian findGuardian(int guardianId) {
        Guardian guardian = guardianDao.findById(guardianId);
        if (guardian == null) {
            throw new BusinessRuleException("No existe un acudiente con ID " + guardianId + ".");
        }
        return guardian;
    }

    public int registerGuardian(Guardian guardian) {
        if (guardianDao.findByDocument(guardian.getDocumentNumber()) != null) {
            throw new BusinessRuleException("Ya existe un acudiente con el documento "
                    + guardian.getDocumentNumber() + ".");
        }
        return guardianDao.insert(guardian);
    }

    public void updateGuardian(Guardian guardian) {
        Guardian sameDocument = guardianDao.findByDocument(guardian.getDocumentNumber());
        if (sameDocument != null && sameDocument.getId() != guardian.getId()) {
            throw new BusinessRuleException("Otro acudiente ya tiene el documento "
                    + guardian.getDocumentNumber() + ".");
        }
        guardianDao.update(guardian);
    }

    /** A guardian cannot be deleted if a student would be left without guardians. */
    public void deleteGuardian(int guardianId) {
        findGuardian(guardianId);
        boolean isOnlyGuardian = studentDao.findByGuardian(guardianId).stream()
                .anyMatch(student -> guardianDao.findByStudent(student.getId()).size() == 1);
        if (isOnlyGuardian) {
            throw new BusinessRuleException("No se puede eliminar: es el único acudiente de al menos un estudiante.");
        }
        guardianDao.delete(guardianId);
    }

    public List<Student> getStudents(int guardianId) {
        findGuardian(guardianId);
        return studentDao.findByGuardian(guardianId);
    }
}
