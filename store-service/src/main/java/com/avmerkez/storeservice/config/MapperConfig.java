package com.avmerkez.storeservice.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.avmerkez.storeservice.mapper.StoreMapper;

/**
 * MapStruct için konfigürasyon sınıfı.
 * MapStruct tarafından oluşturulan mapper implementasyonlarını
 * Spring IoC container'a kaydetmek için kullanılır.
 */
@Configuration
public class MapperConfig {

    /**
     * StoreMapper bean'ini Spring IoC container'a kaydeder.
     * MapStruct, Interface'den implementasyonu oluşturur ancak
     * Spring container'a otomatik kaydetmediği durumlar olabilir.
     * Bu yöntem ile kesin olarak bir StoreMapper bean'i oluşturulur.
     * 
     * @return StoreMapper implementasyonu
     */
    @Bean
    public StoreMapper storeMapper() {
        return Mappers.getMapper(StoreMapper.class);
    }
} 