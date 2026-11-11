package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.ClubMembership;

import java.time.LocalDate;
import java.util.List;

public interface ClubMembershipDao {

    List<ClubMembership> findAll();

    List<ClubMembership> findByClub(int clubId);

    int insert(ClubMembership membership);

    void withdraw(int membershipId, LocalDate withdrawalDate, String reason);
}
