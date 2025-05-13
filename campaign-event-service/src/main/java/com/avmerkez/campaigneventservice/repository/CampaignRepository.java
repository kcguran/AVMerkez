package com.avmerkez.campaigneventservice.repository;

import com.avmerkez.campaigneventservice.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // Find active campaigns (current date is between start and end date)
    List<Campaign> findByStartDateBeforeAndEndDateAfter(LocalDateTime currentTime1, LocalDateTime currentTime2);

    List<Campaign> findByMallId(Long mallId);
    List<Campaign> findByStoreId(Long storeId);
    List<Campaign> findByBrandId(Long brandId);
    List<Campaign> findByMallIdAndStartDateBeforeAndEndDateAfter(Long mallId, LocalDateTime currentTime1, LocalDateTime currentTime2);
    List<Campaign> findByStoreIdAndStartDateBeforeAndEndDateAfter(Long storeId, LocalDateTime currentTime1, LocalDateTime currentTime2);
    List<Campaign> findByBrandIdAndStartDateBeforeAndEndDateAfter(Long brandId, LocalDateTime currentTime1, LocalDateTime currentTime2);

} 