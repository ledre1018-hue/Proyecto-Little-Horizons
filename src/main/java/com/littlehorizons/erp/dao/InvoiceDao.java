package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;

import java.util.List;

public interface InvoiceDao {

    List<Invoice> findAll();
}
