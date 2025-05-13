package com.avmerkez.campaigneventservice.mapper;

import com.avmerkez.campaigneventservice.dto.CampaignDto;
import com.avmerkez.campaigneventservice.dto.CreateCampaignRequest;
import com.avmerkez.campaigneventservice.dto.UpdateCampaignRequest;
import com.avmerkez.campaigneventservice.entity.Campaign;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    CampaignDto toDto(Campaign campaign);

    List<CampaignDto> toDto(List<Campaign> campaigns);

    Campaign toEntity(CreateCampaignRequest request);

    // For updates, ignore null values in the request DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCampaignRequest request, @MappingTarget Campaign campaign);
} 