package com.littlehorizons.erp.controller.report;

import com.littlehorizons.erp.controller.EventController;
import com.littlehorizons.erp.util.TextFormat;

import java.util.Map;

public class EventParticipationReport implements Report {

    private final EventController eventController;

    public EventParticipationReport(EventController eventController) {
        this.eventController = eventController;
    }

    @Override
    public String getTitle() {
        return "Participación por evento";
    }

    @Override
    public ReportTable generate() {
        ReportTable table = new ReportTable(getTitle(), new String[]{"Evento", "Tipo", "Fecha", "Competencia",
                "Participantes"}, new int[]{32, 11, 10, 11, 13});
        Map<Integer, Long> participantsByEvent = eventController.countParticipantsByEvent();
        eventController.listEvents().stream()
                .sorted((first, second) -> Long.compare(participantsByEvent.getOrDefault(second.getId(), 0L),
                        participantsByEvent.getOrDefault(first.getId(), 0L)))
                .forEach(event -> table.addRow(event.getName(), event.getType().getLabel(),
                        TextFormat.date(event.getEventDate()), TextFormat.yesNo(event.hasCompetition()),
                        String.valueOf(participantsByEvent.getOrDefault(event.getId(), 0L))));
        return table;
    }
}
