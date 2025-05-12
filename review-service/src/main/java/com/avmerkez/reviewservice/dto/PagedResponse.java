package com.avmerkez.reviewservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Sayfalama yapılmış yanıt")
public class PagedResponse<T> {
    
    @Schema(description = "Sonuç verileri")
    private List<T> content;
    
    @Schema(description = "Sayfa numarası")
    private int pageNumber;
    
    @Schema(description = "Sayfa başına öğe sayısı")
    private int pageSize;
    
    @Schema(description = "Toplam öğe sayısı")
    private long totalElements;
    
    @Schema(description = "Toplam sayfa sayısı")
    private int totalPages;
    
    @Schema(description = "Son sayfa mı?")
    private boolean last;
} 