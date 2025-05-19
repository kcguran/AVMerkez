package com.avmerkez.mallservice.service.impl;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.entity.Mall;
import com.avmerkez.mallservice.event.MallCreatedEvent;
import com.avmerkez.mallservice.exception.ResourceNotFoundException;
import com.avmerkez.mallservice.mapper.MallMapper;
import com.avmerkez.mallservice.repository.MallRepository;
import com.avmerkez.mallservice.repository.MallSpecification;
import com.avmerkez.mallservice.service.KafkaProducerService;
import com.avmerkez.mallservice.service.MallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class MallServiceImpl implements MallService {
    private final MallRepository mallRepository;
    private final MallMapper mallMapper;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public MallDto createMall(CreateMallRequest request) {
        Mall mall = mallMapper.createRequestToMall(request);
        Mall saved = mallRepository.save(mall);
        
        // Event oluştur ve gönder
        MallCreatedEvent event = new MallCreatedEvent(
            saved.getId(),
            saved.getName(),
            saved.getCity(),
            saved.getDistrict(),
            saved.getDescription(),
            saved.getLatitude(),
            saved.getLongitude()
        );
        kafkaProducerService.sendMallCreatedEvent(event);
        log.info("Mall created with ID: {}", saved.getId());
        return mallMapper.toMallDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MallDto getMallById(Long id) {
        Mall mall = mallRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mall", "id", id));
        log.info("Retrieved mall with ID: {}", id);
        return mallMapper.toMallDto(mall);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallDto> getAllMalls(String city, String district) {
        Specification<Mall> spec = Specification.where(null);
        
        if (city != null && !city.isEmpty()) {
            spec = spec.and(MallSpecification.cityEquals(city));
        }
        
        if (district != null && !district.isEmpty()) {
            spec = spec.and(MallSpecification.districtEquals(district));
        }
        
        List<Mall> malls = mallRepository.findAll(spec);
        log.info("Retrieved {} malls with filters city: {}, district: {}", 
            malls.size(), city, district);
        return mallMapper.toMallDtoList(malls);
    }

    @Override
    @Transactional
    public MallDto updateMall(Long id, UpdateMallRequest updateMallRequest) {
        Mall mall = mallRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mall", "id", id));
        
        mallMapper.updateMallFromRequest(updateMallRequest, mall);
        Mall updatedMall = mallRepository.save(mall);
        log.info("Updated mall with ID: {}", id);
        return mallMapper.toMallDto(updatedMall);
    }

    @Override
    @Transactional
    public void deleteMall(Long id) {
        if (!mallRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mall", "id", id);
        }
        mallRepository.deleteById(id);
        log.info("Deleted mall with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallDto> findMallsNearLocation(double latitude, double longitude, double distanceInKilometers) {
        // Kilometreyi metreye çevir
        double distanceInMeters = distanceInKilometers * 1000;
        
        List<Mall> nearbyMalls = mallRepository.findMallsWithinDistance(latitude, longitude, distanceInMeters);
        log.info("Found {} malls within {}km of coordinates lat:{}, lon:{}", 
            nearbyMalls.size(), distanceInKilometers, latitude, longitude);
        return mallMapper.toMallDtoList(nearbyMalls);
    }
} 