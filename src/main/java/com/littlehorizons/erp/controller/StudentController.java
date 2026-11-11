package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.GuardianDao;
import com.littlehorizons.erp.dao.PaymentDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentGuardian;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.util.List;

/**
 * Business rules for students and their link with guardians.
 */
public class StudentController {

    private final StudentDao studentDao;
    private final GuardianDao guardianDao;
    private final PaymentDao paymentDao;

    public StudentController(StudentDao studentDao, GuardianDao guardianDao, PaymentDao paymentDao) {
        this.studentDao = studentDao;
        this.guardianDao = guardianDao;
        this.paymentDao = paymentDao;
    }

    public List<Student> listStudents() {
        return studentDao.findAll();
    }

    public List<Student> searchByName(String text) {
        String searchedText = text.toLowerCase();
        return studentDao.findAll().stream()
                .filter(student -> student.getFullName().toLowerCase().contains(searchedText))
                .toList();
    }

    public Student findStudent(int studentId) {
        Student student = studentDao.findById(studentId);
        if (student == null) {
            throw new BusinessRuleException("No existe un estudiante con ID " + studentId + ".");
        }
        return student;
    }

    /** A student cannot be registered without at least one guardian. */
    public int registerStudent(Student student, int guardianId, boolean emergencyContact) {
        if (guardianDao.findById(guardianId) == null) {
            throw new BusinessRuleException("El acudiente con ID " + guardianId
                    + " no existe. Registre primero el acudiente.");
        }
        if (studentDao.findByDocument(student.getDocumentNumber()) != null) {
            throw new BusinessRuleException("Ya existe un estudiante con el documento "
                    + student.getDocumentNumber() + ".");
        }
        int studentId = studentDao.insertWithGuardian(student, guardianId, emergencyContact);
        AppLogger.getInstance().info("StudentController.registerStudent", "Student registered with id " + studentId);
        return studentId;
    }

    public void updateStudent(Student student) {
        Student sameDocument = studentDao.findByDocument(student.getDocumentNumber());
        if (sameDocument != null && sameDocument.getId() != student.getId()) {
            throw new BusinessRuleException("Otro estudiante ya tiene el documento " + student.getDocumentNumber() + ".");
        }
        studentDao.update(student);
    }

    /** Financial history must be kept, so students with payments cannot be deleted. */
    public void deleteStudent(int studentId) {
        findStudent(studentId);
        if (!paymentDao.findByStudent(studentId).isEmpty()) {
            throw new BusinessRuleException("El estudiante tiene pagos registrados y no se puede eliminar. "
                    + "Cambie su estado a Retirado.");
        }
        studentDao.delete(studentId);
        AppLogger.getInstance().info("StudentController.deleteStudent", "Student deleted with id " + studentId);
    }

    public List<StudentGuardian> getGuardians(int studentId) {
        findStudent(studentId);
        return guardianDao.findByStudent(studentId);
    }

    public List<Guardian> getEmergencyContacts(int studentId) {
        return getGuardians(studentId).stream()
                .filter(link -> link.isEmergencyContact())
                .map(link -> link.getGuardian())
                .toList();
    }

    public void linkGuardian(int studentId, int guardianId, boolean emergencyContact) {
        Guardian guardian = guardianDao.findById(guardianId);
        if (guardian == null) {
            throw new BusinessRuleException("No existe un acudiente con ID " + guardianId + ".");
        }
        boolean alreadyLinked = getGuardians(studentId).stream()
                .anyMatch(link -> link.getGuardian().getId() == guardianId);
        if (alreadyLinked) {
            throw new BusinessRuleException("Ese acudiente ya está vinculado al estudiante.");
        }
        guardianDao.linkToStudent(new StudentGuardian(studentId, guardian, false, emergencyContact));
    }
}
