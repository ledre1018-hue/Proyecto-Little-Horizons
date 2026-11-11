package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * A student of the school. It has many optional fields, so it is created with Student.Builder.
 */
public class Student extends Person {

    private LocalDate birthDate;
    private Gender gender;
    private String bloodType;
    private String allergies;
    private String medicalConditions;
    private LocalDate admissionDate;
    private StudentStatus status;

    public Student() {
        this.status = StudentStatus.ACTIVE;
    }

    private Student(Builder builder) {
        super(builder.documentType, builder.documentNumber, builder.firstName, builder.lastName);
        setMiddleName(builder.middleName);
        setSecondLastName(builder.secondLastName);
        setBirthDate(builder.birthDate);
        setGender(builder.gender);
        setAddress(builder.address);
        setPhone(builder.phone);
        setEmail(builder.email);
        setBloodType(builder.bloodType);
        setAllergies(builder.allergies);
        setMedicalConditions(builder.medicalConditions);
        setAdmissionDate(builder.admissionDate);
        setStatus(builder.status);
    }

    @Override
    public String getRoleName() {
        return "Estudiante";
    }

    public int getAgeOn(LocalDate date) {
        return birthDate.until(date).getYears();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria y no puede ser futura.");
        }
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("El género es obligatorio.");
        }
        this.gender = gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        String value = optionalText(bloodType);
        if (value != null && !value.matches("(A|B|AB|O)[+-]")) {
            throw new IllegalArgumentException("El tipo de sangre debe ser A, B, AB u O seguido de + o -.");
        }
        this.bloodType = value;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = optionalText(allergies);
    }

    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = optionalText(medicalConditions);
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        if (admissionDate == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria.");
        }
        this.admissionDate = admissionDate;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado del estudiante es obligatorio.");
        }
        this.status = status;
    }

    /**
     * Builder pattern: required data goes in the constructor, optional data in chained methods.
     */
    public static class Builder {

        private final DocumentType documentType;
        private final String documentNumber;
        private final String firstName;
        private final String lastName;
        private String middleName;
        private String secondLastName;
        private LocalDate birthDate;
        private Gender gender;
        private String address;
        private String phone;
        private String email;
        private String bloodType;
        private String allergies;
        private String medicalConditions;
        private LocalDate admissionDate = LocalDate.now();
        private StudentStatus status = StudentStatus.ACTIVE;

        public Builder(DocumentType documentType, String documentNumber, String firstName, String lastName) {
            this.documentType = documentType;
            this.documentNumber = documentNumber;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Builder middleName(String middleName) {
            this.middleName = middleName;
            return this;
        }

        public Builder secondLastName(String secondLastName) {
            this.secondLastName = secondLastName;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder bloodType(String bloodType) {
            this.bloodType = bloodType;
            return this;
        }

        public Builder allergies(String allergies) {
            this.allergies = allergies;
            return this;
        }

        public Builder medicalConditions(String medicalConditions) {
            this.medicalConditions = medicalConditions;
            return this;
        }

        public Builder admissionDate(LocalDate admissionDate) {
            this.admissionDate = admissionDate;
            return this;
        }

        public Builder status(StudentStatus status) {
            this.status = status;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }
}
