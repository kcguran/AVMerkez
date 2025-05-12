package com.avmerkez.mallservice.service;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.entity.Mall;
import com.avmerkez.mallservice.exception.ResourceNotFoundException;
import com.avmerkez.mallservice.mapper.MallMapper;
import com.avmerkez.mallservice.repository.MallRepository;
import com.avmerkez.mallservice.repository.MallSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok: final fieldler için constructor injection
@Slf4j
@Transactional // Servis metotlarını varsayılan olarak transactional yap
public class MallServiceImpl implements MallService {

    private static final double KILOMETERS_TO_METERS = 1000.0;

    private final MallRepository mallRepository;
    private final MallMapper mallMapper;
    private final MallSpecification mallSpecification;

    @Override
    @Transactional // Veri yazma işlemi
    public MallDto createMall(CreateMallRequest createMallRequest) {
        log.info("Creating new mall: {}", createMallRequest.getName());
        Mall mall = mallMapper.createRequestToMall(createMallRequest);
        Mall savedMall = mallRepository.save(mall);
        return mallMapper.toMallDto(savedMall);
    }

    @Override
    @Transactional(readOnly = true) // Sadece okuma işlemi
    public MallDto getMallById(Long id) {
        log.info("Fetching mall by id: {}", id);
        Mall mall = mallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mall", "id", id));
        return mallMapper.toMallDto(mall);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallDto> getAllMalls(String city, String district) {
        log.info("Fetching all malls. City filter: [{}], District filter: [{}]", city, district);
        Specification<Mall> spec = mallSpecification.findByCriteria(city, district);
        List<Mall> malls = mallRepository.findAll(spec);
        return mallMapper.toMallDtoList(malls);
    }

    @Override
    @Transactional // Veri yazma işlemi
    public MallDto updateMall(Long id, UpdateMallRequest updateMallRequest) {
        log.info("Updating mall with id: {}", id);
        Mall existingMall = mallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mall", "id", id));

        // MapStruct, null alanları atlayarak sadece gönderilenleri günceller
        mallMapper.updateMallFromRequest(updateMallRequest, existingMall);

        Mall updatedMall = mallRepository.save(existingMall);
        return mallMapper.toMallDto(updatedMall);
    }

    @Override
    @Transactional // Veri yazma işlemi
    public void deleteMall(Long id) {
        log.info("Deleting mall with id: {}", id);
        if (!mallRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mall", "id", id);
        }
        mallRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallDto> findMallsNearLocation(double latitude, double longitude, double distanceInKilometers) {
        log.info("Finding malls near location (lat: {}, lon: {}) within {} km", latitude, longitude, distanceInKilometers);
        double distanceInMeters = distanceInKilometers * KILOMETERS_TO_METERS;
        List<Mall> malls = mallRepository.findMallsWithinDistance(latitude, longitude, distanceInMeters);
        log.info("Found {} malls near location (lat: {}, lon: {}) within {} km", malls.size(), latitude, longitude, distanceInKilometers);
        return mallMapper.toMallDtoList(malls);
    }
} 