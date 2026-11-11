package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Resource;

import java.util.List;

public interface ResourceDao {

    List<Resource> findAll();

    Resource findById(int resourceId);

    int insert(Resource resource);

    void update(Resource resource);

    void delete(int resourceId);
}
