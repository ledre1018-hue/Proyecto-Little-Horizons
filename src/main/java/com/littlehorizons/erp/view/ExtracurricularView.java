package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.ClubController;
import com.littlehorizons.erp.controller.EventController;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.CompetitionPosition;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.EventType;
import com.littlehorizons.erp.model.ParticipationRole;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.util.ConsoleInput;
import com.littlehorizons.erp.util.ConsolePrinter;
import com.littlehorizons.erp.util.TextFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * Module 4: clubs, memberships, events and participants.
 */
public class ExtracurricularView extends BaseView {

    private static final int MAX_OPTION = 13;
    private static final int NO_SUPERVISOR = 0;

    private final ClubController clubController;
    private final EventController eventController;

    public ExtracurricularView(ClubController clubController, EventController eventController, ConsoleInput input,
                               ConsolePrinter printer) {
        super(input, printer);
        this.clubController = clubController;
        this.eventController = eventController;
    }

    @Override
    public void show() {
        int option;
        do {
            printMenu();
            option = input.readMenuOption(MAX_OPTION);
            switch (option) {
                case 1 -> runAction(() -> listClubs());
                case 2 -> runAction(() -> registerClub());
                case 3 -> runAction(() -> updateClub());
                case 4 -> runAction(() -> deleteClub());
                case 5 -> runAction(() -> enrollInClub());
                case 6 -> runAction(() -> withdrawFromClub());
                case 7 -> runAction(() -> showMembers());
                case 8 -> runAction(() -> listEvents());
                case 9 -> runAction(() -> registerEvent());
                case 10 -> runAction(() -> updateEvent());
                case 11 -> runAction(() -> deleteEvent());
                case 12 -> runAction(() -> registerParticipant());
                case 13 -> runAction(() -> showParticipants());
                case CANCEL_OPTION -> printer.printInfo("Volviendo al menú principal...");
                default -> printUnknownOption();
            }
        } while (option != CANCEL_OPTION);
    }

    private void printMenu() {
        printer.printTitle("Actividades Extracurriculares");
        printer.printSubtitle("Clubes");
        printer.printOption(1, "Listar clubes");
        printer.printOption(2, "Registrar club");
        printer.printOption(3, "Actualizar club");
        printer.printOption(4, "Eliminar club");
        printer.printOption(5, "Inscribir estudiante en un club");
        printer.printOption(6, "Retirar estudiante de un club");
        printer.printOption(7, "Ver miembros de un club");
        printer.printSubtitle("Eventos");
        printer.printOption(8, "Listar eventos");
        printer.printOption(9, "Registrar evento");
        printer.printOption(10, "Actualizar evento");
        printer.printOption(11, "Eliminar evento");
        printer.printOption(12, "Registrar participante en un evento");
        printer.printOption(13, "Ver participantes de un evento");
        printBackOption();
    }

    private void listClubs() {
        printer.printTable(new String[]{"ID", "Club", "Encargado", "Cupo", "Horario", "Activo"},
                new int[]{4, 28, 22, 4, 26, 6}, clubController.listClubs(), club -> new String[]{
                        String.valueOf(club.getId()), club.getName(), TextFormat.orDash(club.getSupervisorName()),
                        String.valueOf(club.getMaxCapacity()), TextFormat.orDash(club.getMeetingSchedule()),
                        TextFormat.yesNo(club.isActive())});
    }

    private void registerClub() {
        Club club = new Club(input.readText("Nombre del club: "), input.readOptionalText("Descripción"),
                readSupervisor(), input.readPositiveInt("Cupo máximo: "));
        club.setMeetingSchedule(input.readOptionalText("Horario de reunión (ej: Martes 3-5 PM)"));
        int clubId = clubController.registerClub(club);
        printer.printSuccess("Club registrado con ID " + clubId + ".");
    }

    private void updateClub() {
        Club club = clubController.findClub(readId("club"));
        printer.printInfo("Presione Enter para conservar el valor actual.");
        club.setName(input.readTextOrKeep("Nombre", club.getName()));
        club.setDescription(input.readTextOrKeep("Descripción", club.getDescription()));
        club.setMaxCapacity(input.readIntOrDefault("Cupo máximo", 1, 500, club.getMaxCapacity()));
        club.setMeetingSchedule(input.readTextOrKeep("Horario de reunión", club.getMeetingSchedule()));
        if (input.readConfirmation("¿Cambiar el profesor encargado?")) {
            club.setSupervisorId(readSupervisor());
        }
        club.setActive(input.readConfirmation("¿El club está activo?"));
        clubController.updateClub(club);
        printer.printSuccess("Club actualizado correctamente.");
    }

    private void deleteClub() {
        Club club = clubController.findClub(readId("club"));
        if (input.readConfirmation("¿Seguro que desea eliminar el club " + club.getName()
                + " y todas sus membresías?")) {
            clubController.deleteClub(club.getId());
            printer.printSuccess("Club eliminado.");
        } else {
            printer.printInfo("Eliminación cancelada.");
        }
    }

    private void enrollInClub() {
        int clubId = readId("club");
        int studentId = readId("estudiante");
        LocalDate joinDate = input.readDateOrDefault("Fecha de ingreso", () -> LocalDate.now());
        int membershipId = clubController.enrollStudent(clubId, studentId, joinDate);
        printer.printSuccess("Estudiante inscrito en el club. Membresía ID " + membershipId + ". Miembros activos: "
                + clubController.countActiveMembers(clubId) + ".");
    }

    private void withdrawFromClub() {
        int clubId = readId("club");
        printMembers(clubController.getMembers(clubId));
        int membershipId = readId("membresía");
        LocalDate withdrawalDate = input.readDateOrDefault("Fecha de retiro", () -> LocalDate.now());
        String reason = input.readText("Motivo del retiro: ");
        clubController.withdrawStudent(clubId, membershipId, withdrawalDate, reason);
        printer.printSuccess("Estudiante retirado del club.");
    }

    private void showMembers() {
        Club club = clubController.findClub(readId("club"));
        printer.printInfo(club.getName() + ": " + clubController.countActiveMembers(club.getId()) + " activos de "
                + club.getMaxCapacity() + " cupos.");
        printMembers(clubController.getMembers(club.getId()));
    }

    private void printMembers(List<ClubMembership> members) {
        printer.printTable(new String[]{"ID", "Estudiante", "Ingreso", "Estado", "Retiro", "Motivo"},
                new int[]{4, 26, 10, 8, 10, 26}, members, membership -> new String[]{
                        String.valueOf(membership.getId()), membership.getStudentName(),
                        TextFormat.date(membership.getJoinDate()), membership.getStatus().getLabel(),
                        TextFormat.date(membership.getWithdrawalDate()),
                        TextFormat.orDash(membership.getWithdrawalReason())});
    }

    private void listEvents() {
        printer.printTable(new String[]{"ID", "Evento", "Tipo", "Fecha", "Lugar", "Competencia"},
                new int[]{4, 30, 10, 10, 22, 11}, eventController.listEvents(), event -> new String[]{
                        String.valueOf(event.getId()), event.getName(), event.getType().getLabel(),
                        TextFormat.date(event.getEventDate()), TextFormat.orDash(event.getPlace()),
                        TextFormat.yesNo(event.hasCompetition())});
    }

    private void registerEvent() {
        SchoolEvent event = new SchoolEvent(input.readText("Nombre del evento: "),
                readOption("Tipo de evento:", EventType.values(), type -> type.getLabel()),
                input.readDate("Fecha del evento"), input.readConfirmation("¿El evento tiene competencia?"));
        event.setDescription(input.readOptionalText("Descripción"));
        event.setPlace(input.readOptionalText("Lugar"));
        int eventId = eventController.registerEvent(event);
        printer.printSuccess("Evento registrado con ID " + eventId + ".");
    }

    private void updateEvent() {
        SchoolEvent event = eventController.findEvent(readId("evento"));
        printer.printInfo("Presione Enter para conservar el valor actual.");
        event.setName(input.readTextOrKeep("Nombre", event.getName()));
        event.setDescription(input.readTextOrKeep("Descripción", event.getDescription()));
        event.setPlace(input.readTextOrKeep("Lugar", event.getPlace()));
        event.setEventDate(input.readDateOrDefault("Fecha", () -> event.getEventDate()));
        if (input.readConfirmation("¿Cambiar el tipo (actual: " + event.getType().getLabel() + ")?")) {
            event.setType(readOption("Tipo de evento:", EventType.values(), type -> type.getLabel()));
        }
        eventController.updateEvent(event);
        printer.printSuccess("Evento actualizado correctamente.");
    }

    private void deleteEvent() {
        SchoolEvent event = eventController.findEvent(readId("evento"));
        if (input.readConfirmation("¿Seguro que desea eliminar el evento " + event.getName() + "?")) {
            eventController.deleteEvent(event.getId());
            printer.printSuccess("Evento eliminado.");
        } else {
            printer.printInfo("Eliminación cancelada.");
        }
    }

    private void registerParticipant() {
        SchoolEvent event = eventController.findEvent(readId("evento"));
        int studentId = readId("estudiante");
        ParticipationRole role = readOption("Rol:", ParticipationRole.values(), option -> option.getLabel());
        CompetitionPosition position = null;
        if (event.hasCompetition()) {
            position = readOption("Posición obtenida:", CompetitionPosition.values(), option -> option.getLabel());
        }
        EventParticipant participant = new EventParticipant(event.getId(), studentId, role, position);
        participant.setNotes(input.readOptionalText("Observaciones"));
        int participantId = eventController.registerParticipant(participant);
        printer.printSuccess("Participación registrada con ID " + participantId + ".");
    }

    private void showParticipants() {
        SchoolEvent event = eventController.findEvent(readId("evento"));
        printer.printInfo(event.getName() + " (" + event.getType().getLabel() + ", " + event.getEventDate() + ")");
        printer.printTable(new String[]{"ID", "Estudiante", "Rol", "Posición"}, new int[]{4, 30, 12, 12},
                eventController.getParticipants(event.getId()), participant -> new String[]{
                        String.valueOf(participant.getStudentId()), participant.getStudentName(),
                        participant.getRole().getLabel(),
                        participant.getPosition() == null ? "-" : participant.getPosition().getLabel()});
    }

    private Integer readSupervisor() {
        int teacherId = input.readInt("ID del profesor encargado (0 = sin encargado): ", 0, Integer.MAX_VALUE);
        return teacherId == NO_SUPERVISOR ? null : teacherId;
    }
}
