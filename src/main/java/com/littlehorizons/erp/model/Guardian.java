package com.littlehorizons.erp.model;

/**
 * Parent or legal tutor responsible for one or more students.
 */
public class Guardian extends Person {

    private static final int MAX_RELATIONSHIP_LENGTH = 50;

    private String relationship;
    private String secondaryPhone;
    private String occupation;
    private boolean financialResponsible;

    public Guardian() {
        this.financialResponsible = true;
    }

    public Guardian(DocumentType documentType, String documentNumber, String firstName, String lastName,
                    String relationship, String phone) {
        super(documentType, documentNumber, firstName, lastName);
        setRelationship(relationship);
        setPhone(phone);
        this.financialResponsible = true;
    }

    @Override
    public String getRoleName() {
        return "Acudiente";
    }

    @Override
    public void setDocumentType(DocumentType documentType) {
        if (documentType == DocumentType.TI) {
            throw new IllegalArgumentException("Un acudiente debe ser mayor de edad (CC, CE o Pasaporte).");
        }
        super.setDocumentType(documentType);
    }

    @Override
    public void setPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("El teléfono principal del acudiente es obligatorio.");
        }
        super.setPhone(phone);
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = requireText(relationship, "parentesco", MAX_RELATIONSHIP_LENGTH);
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = optionalText(secondaryPhone);
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = optionalText(occupation);
    }

    public boolean isFinancialResponsible() {
        return financialResponsible;
    }

    public void setFinancialResponsible(boolean financialResponsible) {
        this.financialResponsible = financialResponsible;
    }
}
