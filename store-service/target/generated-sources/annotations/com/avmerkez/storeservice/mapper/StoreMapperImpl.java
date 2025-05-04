package com.avmerkez.storeservice.mapper;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Store;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T00:32:23+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class StoreMapperImpl implements StoreMapper {

    @Override
    public StoreDto toStoreDto(Store store) {
        if ( store == null ) {
            return null;
        }

        StoreDto storeDto = new StoreDto();

        storeDto.setId( store.getId() );
        storeDto.setMallId( store.getMallId() );
        storeDto.setName( store.getName() );
        storeDto.setCategory( store.getCategory() );
        storeDto.setFloor( store.getFloor() );
        storeDto.setStoreNumber( store.getStoreNumber() );

        return storeDto;
    }

    @Override
    public List<StoreDto> toStoreDtoList(List<Store> stores) {
        if ( stores == null ) {
            return null;
        }

        List<StoreDto> list = new ArrayList<StoreDto>( stores.size() );
        for ( Store store : stores ) {
            list.add( toStoreDto( store ) );
        }

        return list;
    }

    @Override
    public Store createRequestToStore(CreateStoreRequest createStoreRequest) {
        if ( createStoreRequest == null ) {
            return null;
        }

        Store.StoreBuilder store = Store.builder();

        store.name( createStoreRequest.getName() );
        store.mallId( createStoreRequest.getMallId() );
        store.floor( createStoreRequest.getFloor() );
        store.storeNumber( createStoreRequest.getStoreNumber() );
        store.category( createStoreRequest.getCategory() );

        return store.build();
    }

    @Override
    public void updateStoreFromRequest(UpdateStoreRequest updateStoreRequest, Store store) {
        if ( updateStoreRequest == null ) {
            return;
        }

        if ( updateStoreRequest.getName() != null ) {
            store.setName( updateStoreRequest.getName() );
        }
        if ( updateStoreRequest.getFloor() != null ) {
            store.setFloor( updateStoreRequest.getFloor() );
        }
        if ( updateStoreRequest.getStoreNumber() != null ) {
            store.setStoreNumber( updateStoreRequest.getStoreNumber() );
        }
        if ( updateStoreRequest.getCategory() != null ) {
            store.setCategory( updateStoreRequest.getCategory() );
        }
    }
}
