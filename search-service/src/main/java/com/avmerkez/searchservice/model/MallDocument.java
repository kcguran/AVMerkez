package com.avmerkez.searchservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "malls")
public class MallDocument {
    @Id
    private Long id;
    private String name;
    private String city;
    private String district;
    private String description;
    private String category;
    @Field(type = FieldType.GeoPoint)
    private String location; // "lat,lon" formatında
} 