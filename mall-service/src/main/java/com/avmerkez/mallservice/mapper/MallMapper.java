package com.avmerkez.mallservice.mapper;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.entity.Mall;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {FacilityMapper.class})
public interface MallMapper {

    // SRID 4326 (WGS 84) için GeometryFactory.
    // Her dönüşümde yeniden oluşturulacak.
    // Performans kritikse, bir Spring Bean olarak enjekte edilebilir.
    default GeometryFactory getGeometryFactory() {
        return new GeometryFactory(new PrecisionModel(), 4326);
    }

    @AfterMapping
    default void mapLocationToDto(Mall mall, @MappingTarget MallDto dto) {
        if (mall != null && mall.getLocation() != null) {
            dto.setLatitude(mall.getLocation().getY());
            dto.setLongitude(mall.getLocation().getX());
        } else {
            dto.setLatitude(null);
            dto.setLongitude(null);
        }
    }

    @AfterMapping
    default void mapLocationFromCreateRequest(CreateMallRequest request, @MappingTarget Mall mall) {
        if (request != null && request.getLatitude() != null && request.getLongitude() != null) {
            Point point = getGeometryFactory().createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            mall.setLocation(point);
        } else {
            mall.setLocation(null); // Eğer latitude veya longitude null ise location'ı null yap
        }
    }

    @AfterMapping
    default void mapLocationFromUpdateRequest(UpdateMallRequest request, @MappingTarget Mall mall) {
        if (request != null) {
            // NullValuePropertyMappingStrategy.IGNORE nedeniyle, request DTO'sunda 
            // latitude ve longitude alanları hiç gönderilmemişse bu @AfterMapping'in
            // location üzerindeki etkisi olmamalıdır (mevcut değer korunur).
            // Ancak, eğer bu alanlar request DTO'sunda varsa (UpdateMallRequest'te olduğu gibi),
            // o zaman gelen değerlere göre işlem yaparız.

            boolean latitudeProvided = request.getLatitude() != null;
            boolean longitudeProvided = request.getLongitude() != null;

            if (latitudeProvided && longitudeProvided) {
                // Her iki koordinat da doluysa, Point'i oluştur/güncelle.
                Point point = getGeometryFactory().createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
                mall.setLocation(point);
            } else {
                // Eğer DTO latitude/longitude alanlarını içeriyorsa ve bu alanlardan en az biri null ise
                // (veya her ikisi de null ise), bu, kullanıcının konumu temizlemek istediği anlamına gelebilir
                // ya da eksik bilgiyle Point oluşturulamayacağı anlamına gelir. Bu durumda location'ı null yap.
                // Bu kontrol, request objesinin kendisinde bu alanların var olup olmadığını (sadece null olmasını değil)
                // bilmemizi gerektirir. UpdateMallRequest DTO'su her zaman latitude/longitude alanlarını içerir.
                // Bu yüzden, bu alanlardan biri null ise, location'ı null olarak ayarlamak mantıklıdır.
                mall.setLocation(null);
            }
        }
    }

    // updateMallFromRequest için daha basit bir @AfterMapping:
    // @AfterMapping
    // default void mapLocationFromUpdateRequestSimplified(UpdateMallRequest request, @MappingTarget Mall mall) {
    //     if (request != null) {
    //         if (request.getLatitude() != null && request.getLongitude() != null) {
    //             Point point = getGeometryFactory().createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
    //             mall.setLocation(point);
    //         } else if (request.getLatitude() == null && request.getLongitude() == null &&
    //                    (wasFieldSetInRequest(request, "latitude") || wasFieldSetInRequest(request, "longitude"))) {
    //             // Eğer her iki alan da request'te vardı ve null olarak geldiyse, konumu null yap.
    //             // wasFieldSetInRequest gibi bir mekanizma gerekir.
    //             mall.setLocation(null);
    //         }
    //         // Diğer durumda (biri var biri yok, veya ikisi de yok) mevcut location'a dokunma.
    //         // Bu, NullValuePropertyMappingStrategy.IGNORE ile daha uyumludur.
    //     }
    // }

    @Mapping(target = "facilities", source = "facilities")
    MallDto toMallDto(Mall mall);

    List<MallDto> toMallDtoList(List<Mall> malls);

    Mall createRequestToMall(CreateMallRequest createMallRequest);

    // Var olan bir Mall nesnesini UpdateMallRequest ile günceller
    // Null gelen alanlar IGNORE edildiği için sadece dolu gelenler güncellenir
    void updateMallFromRequest(UpdateMallRequest updateMallRequest, @MappingTarget Mall mall);

    // Belirli bir alanın DTO'da açıkça null olarak set edilip edilmediğini kontrol etmek için bir helper.
    // Bu pratikte Reflection veya benzeri bir mekanizma gerektirir ve MapStruct içinde doğrudan kullanımı zordur.
    // Bu yüzden bu tür kontrolleri @AfterMapping içinde yapmak yerine, servis katmanında veya
    // MapStruct Expression Language (eğer destekliyorsa) ile yapmak daha uygun olabilir.
    // Şimdilik update senaryosunda, eğer koordinatlar tam olarak sağlanmazsa (biri null veya ikisi de null ise)
    // ve bu alanlar request DTO'sunda varsa, mevcut konumu null'luyoruz.
    // Eğer alanlar DTO'da hiç yoksa (JSON'da gelmediyse), IGNORE sayesinde dokunulmuyor.
} 