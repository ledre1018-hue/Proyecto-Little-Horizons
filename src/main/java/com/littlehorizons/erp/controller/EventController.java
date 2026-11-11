package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.EventDao;
import com.littlehorizons.erp.dao.EventParticipantDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Business rules for school events and their participants.
 */
public class EventController {

    private final EventDao eventDao;
    private final EventParticipantDao participantDao;
    private final StudentDao studentDao;

    public EventController(EventDao eventDao, EventParticipantDao participantDao, StudentDao studentDao) {
        this.eventDao = eventDao;
        this.participantDao = participantDao;
        this.studentDao = studentDao;
    }

    public List<SchoolEvent> listEvents() {
        return eventDao.findAll();
    }

    public SchoolEvent findEvent(int eventId) {
        SchoolEvent event = eventDao.findById(eventId);
        if (event == null) {
            throw new BusinessRuleException("No existe un evento con ID " + eventId + ".");
        }
        return event;
    }

    public int registerEvent(SchoolEvent event) {
        return eventDao.insert(event);
    }

    public void updateEvent(SchoolEvent event) {
        findEvent(event.getId());
        eventDao.update(event);
    }

    public void deleteEvent(int eventId) {
        findEvent(eventId);
        eventDao.delete(eventId);
    }

    public List<EventParticipant> getParticipants(int eventId) {
        findEvent(eventId);
        return participantDao.findByEvent(eventId);
    }

    /** Number of participants per event id, used by the participation report. */
    public Map<Integer, Long> countParticipantsByEvent() {
        return participantDao.findAll().stream()
                .collect(Collectors.groupingBy(participant -> participant.getEventId(), Collectors.counting()));
    }

    /**
     * Competition events need a position and each podium place (1st, 2nd, 3rd) is given once.
     * Events without competition do not store positions.
     */
    public int registerParticipant(EventParticipant participant) {
        SchoolEvent event = findEvent(participant.getEventId());
        if (studentDao.findById(participant.getStudentId()) == null) {
            throw new BusinessRuleException("No existe un estudiante con ID " + participant.getStudentId() + ".");
        }
        List<EventParticipant> participants = participantDao.findByEvent(event.getId());
        boolean alreadyRegistered = participants.stream()
                .anyMatch(saved -> saved.getStudentId() == participant.getStudentId());
        if (alreadyRegistered) {
            throw new BusinessRuleException("El estudiante ya está registrado en este evento.");
        }
        if (event.hasCompetition()) {
            validatePosition(participant, participants);
        } else {
            participant.setPosition(null);
        }
        return participantDao.insert(participant);
    }

    private void validatePosition(EventParticipant participant, List<EventParticipant> participants) {
        if (participant.getPosition() == null) {
            throw new BusinessRuleException("El evento tiene competencia: debe indicar la posición obtenida.");
        }
        boolean podiumTaken = participant.isPodium() && participants.stream()
                .anyMatch(saved -> saved.getPosition() == participant.getPosition());
        if (podiumTaken) {
            throw new BusinessRuleException("La posición " + participant.getPosition().getLabel()
                    + " ya fue asignada en este evento.");
        }
    }
}
