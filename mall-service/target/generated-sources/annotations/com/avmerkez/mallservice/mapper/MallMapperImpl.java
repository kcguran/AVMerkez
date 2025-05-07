package com.avmerkez.mallservice.mapper;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.entity.Mall;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-08T00:42:45+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class MallMapperImpl implements MallMapper {

    @Override
    public MallDto toMallDto(Mall mall) {
        if ( mall == null ) {
            return null;
        }

        MallDto mallDto = new MallDto();

        mallDto.setId( mall.getId() );
        mallDto.setName( mall.getName() );
        mallDto.setCity( mall.getCity() );
        mallDto.setDistrict( mall.getDistrict() );

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

        mall.name( createMallRequest.getName() );
        mall.address( createMallRequest.getAddress() );
        mall.city( createMallRequest.getCity() );
        mall.district( createMallRequest.getDistrict() );

        return mall.build();
    }

    @Override
    public void updateMallFromRequest(UpdateMallRequest updateMallRequest, Mall mall) {
        if ( updateMallRequest == null ) {
            return;
        }

        if ( updateMallRequest.getName() != null ) {
            mall.setName( updateMallRequest.getName() );
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
    }
}
