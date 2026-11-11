package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.ResourceCategory;

import java.util.List;

/** Categories are a fixed catalog, so this DAO only reads. */
public interface ResourceCategoryDao {

    List<ResourceCategory> findAll();
}
