package com.avmerkez.campaigneventservice.service;

import com.avmerkez.campaigneventservice.dto.CampaignDto;
import com.avmerkez.campaigneventservice.dto.CreateCampaignRequest;
import com.avmerkez.campaigneventservice.dto.UpdateCampaignRequest;

import java.util.List;

public interface CampaignService {

    CampaignDto createCampaign(CreateCampaignRequest request);

    CampaignDto getCampaignById(Long id);

    List<CampaignDto> getAllCampaigns();

    List<CampaignDto> getActiveCampaigns();

    List<CampaignDto> getActiveCampaignsByMallId(Long mallId);

    List<CampaignDto> getActiveCampaignsByStoreId(Long storeId);

    List<CampaignDto> getActiveCampaignsByBrandId(Long brandId);

    CampaignDto updateCampaign(Long id, UpdateCampaignRequest request);

    void deleteCampaign(Long id);
} 