package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.PaymentConcept;

import java.util.List;

public interface PaymentConceptDao {

    List<PaymentConcept> findAll();

    PaymentConcept findById(int conceptId);

    int insert(PaymentConcept concept);

    void update(PaymentConcept concept);

    void delete(int conceptId);
}
