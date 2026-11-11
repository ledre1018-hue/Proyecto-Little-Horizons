package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Teacher of the school.
 */
public class Teacher extends Person {

    private String specialization;
    private LocalDate hireDate;
    private TeacherStatus status;

    public Teacher() {
        this.status = TeacherStatus.ACTIVE;
    }

    public Teacher(DocumentType documentType, String documentNumber, String firstName, String lastName,
                   String email, LocalDate hireDate) {
        super(documentType, documentNumber, firstName, lastName);
        setEmail(email);
        setHireDate(hireDate);
        this.status = TeacherStatus.ACTIVE;
    }

    @Override
    public String getRoleName() {
        return "Profesor";
    }

    @Override
    public void setDocumentType(DocumentType documentType) {
        if (documentType == DocumentType.TI) {
            throw new IllegalArgumentException("Un profesor debe tener CC, CE o Pasaporte.");
        }
        super.setDocumentType(documentType);
    }

    @Override
    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo del profesor es obligatorio.");
        }
        super.setEmail(email);
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = optionalText(specialization);
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        if (hireDate == null) {
            throw new IllegalArgumentException("La fecha de contratación es obligatoria.");
        }
        this.hireDate = hireDate;
    }

    public TeacherStatus getStatus() {
        return status;
    }

    public void setStatus(TeacherStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado del profesor es obligatorio.");
        }
        this.status = status;
    }
}
