package com.littlehorizons.erp.model;

/**
 * Common data of every person in the school: students, guardians and teachers.
 */
public abstract class Person {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DOCUMENT_LENGTH = 20;

    private int id;
    private DocumentType documentType;
    private String documentNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String phone;
    private String email;
    private String address;

    protected Person() {
    }

    protected Person(DocumentType documentType, String documentNumber, String firstName, String lastName) {
        setDocumentType(documentType);
        setDocumentNumber(documentNumber);
        setFirstName(firstName);
        setLastName(lastName);
    }

    /** Role shown on screen, for example "Estudiante". */
    public abstract String getRoleName();

    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        if (middleName != null) {
            fullName.append(' ').append(middleName);
        }
        fullName.append(' ').append(lastName);
        if (secondLastName != null) {
            fullName.append(' ').append(secondLastName);
        }
        return fullName.toString();
    }

    protected static String requireText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo " + fieldName + " es obligatorio.");
        }
        if (value.trim().length() > maxLength) {
            throw new IllegalArgumentException("El campo " + fieldName + " admite máximo " + maxLength
                    + " caracteres.");
        }
        return value.trim();
    }

    protected static String optionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        if (documentType == null) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio.");
        }
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        String value = requireText(documentNumber, "número de documento", MAX_DOCUMENT_LENGTH);
        if (!value.matches("[A-Za-z0-9]+")) {
            throw new IllegalArgumentException("El número de documento solo admite letras y números.");
        }
        this.documentNumber = value;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = requireText(firstName, "primer nombre", MAX_NAME_LENGTH);
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = optionalText(middleName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = requireText(lastName, "primer apellido", MAX_NAME_LENGTH);
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = optionalText(secondLastName);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        String value = optionalText(phone);
        if (value != null && !value.matches("[0-9+ ]{7,20}")) {
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 20 dígitos.");
        }
        this.phone = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String value = optionalText(email);
        if (value != null && !value.contains("@")) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        this.email = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = optionalText(address);
    }
}
