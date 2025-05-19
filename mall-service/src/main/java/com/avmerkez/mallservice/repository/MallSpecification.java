package com.avmerkez.mallservice.repository;

import com.avmerkez.mallservice.entity.Mall;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MallSpecification {

    public Specification<Mall> findByCriteria(String city, String district) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("city")), city.toLowerCase()));
            }

            if (district != null && !district.isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("district")), district.toLowerCase()));
            }
            
            // Add other criteria here as needed (e.g., name, services etc.)

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Şehir adına göre filtreleme yapar
     */
    public static Specification<Mall> cityEquals(String city) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(criteriaBuilder.lower(root.get("city")), city.toLowerCase());
    }
    
    /**
     * İlçe adına göre filtreleme yapar
     */
    public static Specification<Mall> districtEquals(String district) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(criteriaBuilder.lower(root.get("district")), district.toLowerCase());
    }
} 