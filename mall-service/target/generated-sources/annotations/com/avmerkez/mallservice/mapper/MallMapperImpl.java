package com.avmerkez.mallservice.mapper;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.entity.Mall;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-19T03:09:14+0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class MallMapperImpl implements MallMapper {

    @Autowired
    private FacilityMapper facilityMapper;

    @Override
    public MallDto toMallDto(Mall mall) {
        if ( mall == null ) {
            return null;
        }

        MallDto mallDto = new MallDto();

        mallDto.setFacilities( facilityMapper.toFacilityDtoList( mall.getFacilities() ) );
        mallDto.setAddress( mall.getAddress() );
        mallDto.setCity( mall.getCity() );
        mallDto.setDistrict( mall.getDistrict() );
        mallDto.setId( mall.getId() );
        mallDto.setName( mall.getName() );
        mallDto.setPhoneNumber( mall.getPhoneNumber() );
        mallDto.setWebsite( mall.getWebsite() );
        mallDto.setWorkingHours( mall.getWorkingHours() );

        mapLocationToDto( mall, mallDto );

        return mallDto;
    }

    @Override
    public List<MallDto> toMallDtoList(List<Mall> malls) {
        if ( malls == null ) {
            return null;
        }

        List<MallDto> list = new ArrayList<MallDto>( malls.size() );
        for ( Mall mall : malls ) {
            list.add( toMallDto( mall ) );
        }

        return list;
    }

    @Override
    public Mall createRequestToMall(CreateMallRequest createMallRequest) {
        if ( createMallRequest == null ) {
            return null;
        }

        Mall.MallBuilder mall = Mall.builder();

        mall.address( createMallRequest.getAddress() );
        mall.city( createMallRequest.getCity() );
        mall.district( createMallRequest.getDistrict() );
        mall.name( createMallRequest.getName() );
        mall.phoneNumber( createMallRequest.getPhoneNumber() );
        mall.website( createMallRequest.getWebsite() );
        mall.workingHours( createMallRequest.getWorkingHours() );

        return mall.build();
    }

    @Override
    public void updateMallFromRequest(UpdateMallRequest updateMallRequest, Mall mall) {
        if ( updateMallRequest == null ) {
            return;
        }

        if ( updateMallRequest.getAddress() != null ) {
            mall.setAddress( updateMallRequest.getAddress() );
        }
        if ( updateMallRequest.getCity() != null ) {
            mall.setCity( updateMallRequest.getCity() );
        }
        if ( updateMallRequest.getDistrict() != null ) {
            mall.setDistrict( updateMallRequest.getDistrict() );
        }
        if ( updateMallRequest.getName() != null ) {
            mall.setName( updateMallRequest.getName() );
        }
        if ( updateMallRequest.getPhoneNumber() != null ) {
            mall.setPhoneNumber( updateMallRequest.getPhoneNumber() );
        }
        if ( updateMallRequest.getWebsite() != null ) {
            mall.setWebsite( updateMallRequest.getWebsite() );
        }
        if ( updateMallRequest.getWorkingHours() != null ) {
            mall.setWorkingHours( updateMallRequest.getWorkingHours() );
        }

        mapLocationFromUpdateRequest( updateMallRequest, mall );
    }
}
