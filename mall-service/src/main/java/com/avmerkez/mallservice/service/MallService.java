package com.avmerkez.mallservice.service;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;

import java.util.List;

public interface MallService {
    MallDto createMall(CreateMallRequest createMallRequest);
    MallDto getMallById(Long id);
    List<MallDto> getAllMalls();
    MallDto updateMall(Long id, UpdateMallRequest updateMallRequest);
    void deleteMall(Long id);
} 