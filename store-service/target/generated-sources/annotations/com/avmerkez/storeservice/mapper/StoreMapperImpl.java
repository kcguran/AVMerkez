package com.avmerkez.storeservice.mapper;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDetailDto;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Brand;
import com.avmerkez.storeservice.entity.Category;
import com.avmerkez.storeservice.entity.Store;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-18T01:35:45+0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class StoreMapperImpl implements StoreMapper {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public StoreDto toStoreDto(Store store) {
        if ( store == null ) {
            return null;
        }

        StoreDto storeDto = new StoreDto();

        storeDto.setCategoryId( storeCategoryId( store ) );
        storeDto.setBrandId( storeBrandId( store ) );
        storeDto.setContactInformation( store.getContactInformation() );
        storeDto.setDescription( store.getDescription() );
        storeDto.setFloor( store.getFloor() );
        storeDto.setId( store.getId() );
        storeDto.setLogoUrl( store.getLogoUrl() );
        storeDto.setMallId( store.getMallId() );
        storeDto.setName( store.getName() );

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
    public StoreDetailDto toStoreDetailDto(Store store) {
        if ( store == null ) {
            return null;
        }

        StoreDetailDto storeDetailDto = new StoreDetailDto();

        storeDetailDto.setBrand( brandMapper.toBrandDto( store.getBrand() ) );
        storeDetailDto.setCategory( categoryMapper.toCategoryDto( store.getCategory() ) );
        storeDetailDto.setContactInformation( store.getContactInformation() );
        storeDetailDto.setDescription( store.getDescription() );
        storeDetailDto.setFloor( store.getFloor() );
        storeDetailDto.setId( store.getId() );
        storeDetailDto.setLogoUrl( store.getLogoUrl() );
        storeDetailDto.setMallId( store.getMallId() );
        storeDetailDto.setName( store.getName() );

        return storeDetailDto;
    }

    @Override
    public List<StoreDetailDto> toStoreDetailDtoList(List<Store> stores) {
        if ( stores == null ) {
            return null;
        }

        List<StoreDetailDto> list = new ArrayList<StoreDetailDto>( stores.size() );
        for ( Store store : stores ) {
            list.add( toStoreDetailDto( store ) );
        }

        return list;
    }

    @Override
    public Store toStore(CreateStoreRequest createStoreRequest) {
        if ( createStoreRequest == null ) {
            return null;
        }

        Store store = new Store();

        store.setContactInformation( createStoreRequest.getContactInformation() );
        store.setDescription( createStoreRequest.getDescription() );
        store.setFloor( createStoreRequest.getFloor() );
        store.setLogoUrl( createStoreRequest.getLogoUrl() );
        store.setMallId( createStoreRequest.getMallId() );
        store.setName( createStoreRequest.getName() );

        return store;
    }

    @Override
    public void updateStoreFromRequest(UpdateStoreRequest updateStoreRequest, Store store) {
        if ( updateStoreRequest == null ) {
            return;
        }

        if ( updateStoreRequest.getContactInformation() != null ) {
            store.setContactInformation( updateStoreRequest.getContactInformation() );
        }
        if ( updateStoreRequest.getDescription() != null ) {
            store.setDescription( updateStoreRequest.getDescription() );
        }
        if ( updateStoreRequest.getFloor() != null ) {
            store.setFloor( updateStoreRequest.getFloor() );
        }
        if ( updateStoreRequest.getLogoUrl() != null ) {
            store.setLogoUrl( updateStoreRequest.getLogoUrl() );
        }
        if ( updateStoreRequest.getMallId() != null ) {
            store.setMallId( updateStoreRequest.getMallId() );
        }
        if ( updateStoreRequest.getName() != null ) {
            store.setName( updateStoreRequest.getName() );
        }
    }

    private Long storeCategoryId(Store store) {
        if ( store == null ) {
            return null;
        }
        Category category = store.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long storeBrandId(Store store) {
        if ( store == null ) {
            return null;
        }
        Brand brand = store.getBrand();
        if ( brand == null ) {
            return null;
        }
        Long id = brand.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
