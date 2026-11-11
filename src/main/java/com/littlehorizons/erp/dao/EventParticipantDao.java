package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.EventParticipant;

import java.util.List;

public interface EventParticipantDao {

    List<EventParticipant> findAll();

    List<EventParticipant> findByEvent(int eventId);

    int insert(EventParticipant participant);
}
