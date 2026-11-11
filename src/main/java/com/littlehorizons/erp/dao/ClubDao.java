package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Club;

import java.util.List;

public interface ClubDao {

    List<Club> findAll();

    Club findById(int clubId);

    int insert(Club club);

    void update(Club club);

    void delete(int clubId);
}
