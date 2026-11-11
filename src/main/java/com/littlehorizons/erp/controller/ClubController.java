package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.ClubDao;
import com.littlehorizons.erp.dao.ClubMembershipDao;
import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentStatus;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Business rules for clubs and memberships (capacity and duplicates).
 */
public class ClubController {

    private final ClubDao clubDao;
    private final ClubMembershipDao membershipDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;

    public ClubController(ClubDao clubDao, ClubMembershipDao membershipDao, StudentDao studentDao,
                          TeacherDao teacherDao) {
        this.clubDao = clubDao;
        this.membershipDao = membershipDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
    }

    public List<Club> listClubs() {
        return clubDao.findAll();
    }

    public Club findClub(int clubId) {
        Club club = clubDao.findById(clubId);
        if (club == null) {
            throw new BusinessRuleException("No existe un club con ID " + clubId + ".");
        }
        return club;
    }

    public int registerClub(Club club) {
        validateSupervisor(club.getSupervisorId());
        return clubDao.insert(club);
    }

    public void updateClub(Club club) {
        findClub(club.getId());
        validateSupervisor(club.getSupervisorId());
        long activeMembers = countActiveMembers(club.getId());
        if (club.getMaxCapacity() < activeMembers) {
            throw new BusinessRuleException("El cupo no puede ser menor que los " + activeMembers
                    + " miembros activos del club.");
        }
        clubDao.update(club);
    }

    public void deleteClub(int clubId) {
        findClub(clubId);
        clubDao.delete(clubId);
    }

    public List<ClubMembership> getMembers(int clubId) {
        findClub(clubId);
        return membershipDao.findByClub(clubId);
    }

    public long countActiveMembers(int clubId) {
        return membershipDao.findByClub(clubId).stream()
                .filter(membership -> membership.isActive())
                .count();
    }

    /** Active members per club id, used by the participation report. */
    public Map<Integer, Long> countActiveMembersByClub() {
        return membershipDao.findAll().stream()
                .filter(membership -> membership.isActive())
                .collect(Collectors.groupingBy(membership -> membership.getClubId(), Collectors.counting()));
    }

    public int enrollStudent(int clubId, int studentId, LocalDate joinDate) {
        Club club = findClub(clubId);
        if (!club.isActive()) {
            throw new BusinessRuleException("El club no está activo.");
        }
        Student student = studentDao.findById(studentId);
        if (student == null || student.getStatus() != StudentStatus.ACTIVE) {
            throw new BusinessRuleException("El estudiante no existe o no está activo.");
        }
        List<ClubMembership> members = membershipDao.findByClub(clubId);
        boolean alreadyActive = members.stream()
                .anyMatch(membership -> membership.getStudentId() == studentId && membership.isActive());
        if (alreadyActive) {
            throw new BusinessRuleException("El estudiante ya es miembro activo de este club.");
        }
        long activeMembers = members.stream().filter(membership -> membership.isActive()).count();
        if (activeMembers >= club.getMaxCapacity()) {
            throw new BusinessRuleException("El club alcanzó su cupo máximo de " + club.getMaxCapacity()
                    + " estudiantes.");
        }
        return membershipDao.insert(new ClubMembership(studentId, clubId, joinDate));
    }

    public void withdrawStudent(int clubId, int membershipId, LocalDate withdrawalDate, String reason) {
        List<ClubMembership> found = getMembers(clubId).stream()
                .filter(membership -> membership.getId() == membershipId)
                .toList();
        if (found.isEmpty() || !found.get(0).isActive()) {
            throw new BusinessRuleException("No existe una membresía activa con ID " + membershipId + " en este club.");
        }
        if (withdrawalDate.isBefore(found.get(0).getJoinDate())) {
            throw new BusinessRuleException("La fecha de retiro no puede ser anterior a la fecha de ingreso.");
        }
        membershipDao.withdraw(membershipId, withdrawalDate, reason);
    }

    private void validateSupervisor(Integer supervisorId) {
        if (supervisorId != null && teacherDao.findById(supervisorId) == null) {
            throw new BusinessRuleException("No existe un profesor con ID " + supervisorId + ".");
        }
    }
}
