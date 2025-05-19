package com.avmerkez.mallservice.repository;

import com.avmerkez.mallservice.entity.Facility;
import com.avmerkez.mallservice.entity.Mall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findByMall(Mall mall);
} 