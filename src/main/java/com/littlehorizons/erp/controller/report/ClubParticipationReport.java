package com.littlehorizons.erp.controller.report;

import com.littlehorizons.erp.controller.ClubController;
import com.littlehorizons.erp.util.TextFormat;

import java.util.Map;

public class ClubParticipationReport implements Report {

    private final ClubController clubController;

    public ClubParticipationReport(ClubController clubController) {
        this.clubController = clubController;
    }

    @Override
    public String getTitle() {
        return "Participación por club (miembros activos)";
    }

    @Override
    public ReportTable generate() {
        ReportTable table = new ReportTable(getTitle(), new String[]{"Club", "Encargado", "Activos", "Cupo",
                "Ocupación"}, new int[]{28, 24, 7, 5, 9});
        Map<Integer, Long> membersByClub = clubController.countActiveMembersByClub();
        clubController.listClubs().stream()
                .filter(club -> club.isActive())
                .sorted((first, second) -> Long.compare(membersByClub.getOrDefault(second.getId(), 0L),
                        membersByClub.getOrDefault(first.getId(), 0L)))
                .forEach(club -> {
                    long members = membersByClub.getOrDefault(club.getId(), 0L);
                    double occupancy = members * 100.0 / club.getMaxCapacity();
                    table.addRow(club.getName(), TextFormat.orDash(club.getSupervisorName()), String.valueOf(members),
                            String.valueOf(club.getMaxCapacity()), String.format("%.0f%%", occupancy));
                });
        return table;
    }
}
