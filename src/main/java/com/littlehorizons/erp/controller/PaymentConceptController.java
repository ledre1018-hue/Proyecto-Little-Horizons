package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.PaymentConceptDao;
import com.littlehorizons.erp.dao.StudentConceptPriceDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.model.StudentConceptPrice;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business rules for the payment concept catalog and the custom prices per student.
 */
public class PaymentConceptController {

    private final PaymentConceptDao conceptDao;
    private final StudentConceptPriceDao priceDao;
    private final StudentDao studentDao;

    public PaymentConceptController(PaymentConceptDao conceptDao, StudentConceptPriceDao priceDao,
                                    StudentDao studentDao) {
        this.conceptDao = conceptDao;
        this.priceDao = priceDao;
        this.studentDao = studentDao;
    }

    public List<PaymentConcept> listConcepts() {
        return conceptDao.findAll();
    }

    public List<PaymentConcept> listActiveConcepts() {
        return conceptDao.findAll().stream()
                .filter(concept -> concept.isActive())
                .toList();
    }

    public PaymentConcept findConcept(int conceptId) {
        PaymentConcept concept = conceptDao.findById(conceptId);
        if (concept == null) {
            throw new BusinessRuleException("No existe un concepto de pago con ID " + conceptId + ".");
        }
        return concept;
    }

    public int registerConcept(PaymentConcept concept) {
        boolean codeExists = conceptDao.findAll().stream()
                .anyMatch(saved -> saved.getCode().equalsIgnoreCase(concept.getCode()));
        if (codeExists) {
            throw new BusinessRuleException("Ya existe un concepto con el código " + concept.getCode() + ".");
        }
        return conceptDao.insert(concept);
    }

    public void updateConcept(PaymentConcept concept) {
        findConcept(concept.getId());
        conceptDao.update(concept);
    }

    public void deleteConcept(int conceptId) {
        findConcept(conceptId);
        conceptDao.delete(conceptId);
    }

    public List<StudentConceptPrice> listCustomPrices() {
        return priceDao.findAll();
    }

    public int registerCustomPrice(StudentConceptPrice price) {
        if (studentDao.findById(price.getStudentId()) == null) {
            throw new BusinessRuleException("No existe un estudiante con ID " + price.getStudentId() + ".");
        }
        findConcept(price.getConceptId());
        if (priceDao.findByStudentAndConcept(price.getStudentId(), price.getConceptId(),
                price.getAcademicYear()) != null) {
            throw new BusinessRuleException("El estudiante ya tiene un valor personalizado para ese concepto y año.");
        }
        return priceDao.insert(price);
    }

    public void deleteCustomPrice(int priceId) {
        boolean exists = priceDao.findAll().stream().anyMatch(price -> price.getId() == priceId);
        if (!exists) {
            throw new BusinessRuleException("No existe un valor personalizado con ID " + priceId + ".");
        }
        priceDao.delete(priceId);
    }

    /** The custom price of the student if it exists; otherwise the base amount of the concept. */
    public BigDecimal getSuggestedAmount(int studentId, int conceptId, int academicYear) {
        StudentConceptPrice customPrice = priceDao.findByStudentAndConcept(studentId, conceptId, academicYear);
        if (customPrice != null) {
            return customPrice.getCustomAmount();
        }
        return findConcept(conceptId).getBaseAmount();
    }
}
