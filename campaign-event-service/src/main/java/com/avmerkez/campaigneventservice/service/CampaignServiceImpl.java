package com.avmerkez.campaigneventservice.service;

import com.avmerkez.campaigneventservice.dto.CampaignDto;
import com.avmerkez.campaigneventservice.dto.CreateCampaignRequest;
import com.avmerkez.campaigneventservice.dto.UpdateCampaignRequest;
import com.avmerkez.campaigneventservice.entity.Campaign;
import com.avmerkez.campaigneventservice.exception.ResourceNotFoundException; // Need to create this exception
import com.avmerkez.campaigneventservice.mapper.CampaignMapper;
import com.avmerkez.campaigneventservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    // private final MallServiceClient mallServiceClient; // Inject when validation is needed
    // private final StoreServiceClient storeServiceClient; // Inject when validation is needed
    // private final BrandServiceClient brandServiceClient; // Inject when validation is needed

    @Override
    @Transactional
    public CampaignDto createCampaign(CreateCampaignRequest request) {
        log.info("Creating new campaign: {}", request.getName());
        // TODO: Add validation for mallId, storeId, brandId using Feign clients if they are not null
        // validateExternalIds(request.getMallId(), request.getStoreId(), request.getBrandId());

        Campaign campaign = campaignMapper.toEntity(request);
        Campaign savedCampaign = campaignRepository.save(campaign);
        log.info("Campaign created successfully with id: {}", savedCampaign.getId());
        return campaignMapper.toDto(savedCampaign);
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignDto getCampaignById(Long id) {
        log.debug("Fetching campaign with id: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", "id", id));
        return campaignMapper.toDto(campaign);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignDto> getAllCampaigns() {
        log.debug("Fetching all campaigns");
        return campaignMapper.toDto(campaignRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignDto> getActiveCampaigns() {
        log.debug("Fetching active campaigns");
        LocalDateTime now = LocalDateTime.now();
        return campaignMapper.toDto(campaignRepository.findByStartDateBeforeAndEndDateAfter(now, now));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignDto> getActiveCampaignsByMallId(Long mallId) {
        log.debug("Fetching active campaigns for mallId: {}", mallId);
        LocalDateTime now = LocalDateTime.now();
        return campaignMapper.toDto(campaignRepository.findByMallIdAndStartDateBeforeAndEndDateAfter(mallId, now, now));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignDto> getActiveCampaignsByStoreId(Long storeId) {
         log.debug("Fetching active campaigns for storeId: {}", storeId);
         LocalDateTime now = LocalDateTime.now();
         return campaignMapper.toDto(campaignRepository.findByStoreIdAndStartDateBeforeAndEndDateAfter(storeId, now, now));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignDto> getActiveCampaignsByBrandId(Long brandId) {
        log.debug("Fetching active campaigns for brandId: {}", brandId);
        LocalDateTime now = LocalDateTime.now();
        return campaignMapper.toDto(campaignRepository.findByBrandIdAndStartDateBeforeAndEndDateAfter(brandId, now, now));
    }

    @Override
    @Transactional
    public CampaignDto updateCampaign(Long id, UpdateCampaignRequest request) {
        log.info("Updating campaign with id: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", "id", id));

        // TODO: Add validation if mallId, storeId, or brandId are being updated

        campaignMapper.updateEntityFromRequest(request, campaign);
        Campaign updatedCampaign = campaignRepository.save(campaign);
        log.info("Campaign updated successfully with id: {}", updatedCampaign.getId());
        return campaignMapper.toDto(updatedCampaign);
    }

    @Override
    @Transactional
    public void deleteCampaign(Long id) {
        log.info("Deleting campaign with id: {}", id);
        if (!campaignRepository.existsById(id)) {
            throw new ResourceNotFoundException("Campaign", "id", id);
        }
        campaignRepository.deleteById(id);
        log.info("Campaign deleted successfully with id: {}", id);
    }

    // Placeholder for validation logic
    // private void validateExternalIds(Long mallId, Long storeId, Long brandId) {
    //     if (mallId != null) { /* Call mallServiceClient.checkMallExists(mallId); */ }
    //     if (storeId != null) { /* Call storeServiceClient.checkStoreExists(storeId); */ }
    //     if (brandId != null) { /* Call brandServiceClient.checkBrandExists(brandId); */ }
    // }
} 