package com.avmerkez.mallservice.repository;

import com.avmerkez.mallservice.entity.Mall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MallRepository extends JpaRepository<Mall, Long>, JpaSpecificationExecutor<Mall> {
    // İleride özel sorgular gerekirse buraya eklenebilir
    // Örneğin: List<Mall> findByCity(String city);

    // Removed findByCityAndDistrict and findByCity as Specifications will handle this

    /**
     * Finds malls within a given distance (in meters) from a specified point (SRID 4326).
     * Uses ST_DWithin for spherical distance calculation on geography type for accuracy with lat/lon.
     * The location column in the database is expected to be of type GEOMETRY with SRID 4326.
     * To use ST_DWithin with geography, we cast the geometry column to geography.
     * The input point is created using ST_SetSRID(ST_MakePoint(longitude, latitude), 4326).
     */
    @Query(value = "SELECT m.* FROM malls m WHERE ST_DWithin(" +
            "    CAST(m.location AS geography), " +
            "    ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, " +
            "    :distanceInMeters" +
            ")", nativeQuery = true)
    List<Mall> findMallsWithinDistance(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distanceInMeters") double distanceInMeters
    );
} 