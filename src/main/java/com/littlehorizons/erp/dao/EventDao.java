package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.SchoolEvent;

import java.util.List;

public interface EventDao {

    List<SchoolEvent> findAll();

    SchoolEvent findById(int eventId);

    int insert(SchoolEvent event);

    void update(SchoolEvent event);

    void delete(int eventId);
}
