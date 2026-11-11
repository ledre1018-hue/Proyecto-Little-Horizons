package com.littlehorizons.erp.model;

/**
 * Link between a student and one of their guardians (association).
 */
public class StudentGuardian {

    private int studentId;
    private Guardian guardian;
    private boolean primaryGuardian;
    private boolean emergencyContact;

    public StudentGuardian() {
    }

    public StudentGuardian(int studentId, Guardian guardian, boolean primaryGuardian, boolean emergencyContact) {
        this.studentId = studentId;
        setGuardian(guardian);
        this.primaryGuardian = primaryGuardian;
        this.emergencyContact = emergencyContact;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Guardian getGuardian() {
        return guardian;
    }

    public void setGuardian(Guardian guardian) {
        if (guardian == null) {
            throw new IllegalArgumentException("El acudiente es obligatorio.");
        }
        this.guardian = guardian;
    }

    public boolean isPrimaryGuardian() {
        return primaryGuardian;
    }

    public void setPrimaryGuardian(boolean primaryGuardian) {
        this.primaryGuardian = primaryGuardian;
    }

    public boolean isEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(boolean emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}
