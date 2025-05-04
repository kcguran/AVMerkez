package com.avmerkez.mallservice.service;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.entity.Mall;
import com.avmerkez.mallservice.exception.ResourceNotFoundException;
import com.avmerkez.mallservice.mapper.MallMapper;
import com.avmerkez.mallservice.repository.MallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok: final fieldler için constructor injection
@Transactional // Tüm public metodlar için transaction yönetimi
public class MallServiceImpl implements MallService {

    private final MallRepository mallRepository;
    private final MallMapper mallMapper;

    @Override
    @Transactional // Veri yazma işlemi
    public MallDto createMall(CreateMallRequest createMallRequest) {
        Mall mall = mallMapper.createRequestToMall(createMallRequest);
        Mall savedMall = mallRepository.save(mall);
        return mallMapper.toMallDto(savedMall);
    }

    @Override
    @Transactional(readOnly = true) // Sadece okuma işlemi
    public MallDto getMallById(Long id) {
        Mall mall = mallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mall", "id", id));
        return mallMapper.toMallDto(mall);
    }

    @Override
    @Transactional(readOnly = true) // Sadece okuma işlemi
    public List<MallDto> getAllMalls() {
        List<Mall> malls = mallRepository.findAll();
        return mallMapper.toMallDtoList(malls);
    }

    @Override
    @Transactional // Veri yazma işlemi
    public MallDto updateMall(Long id, UpdateMallRequest updateMallRequest) {
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
        if (!mallRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mall", "id", id);
        }
        mallRepository.deleteById(id);
    }
} 