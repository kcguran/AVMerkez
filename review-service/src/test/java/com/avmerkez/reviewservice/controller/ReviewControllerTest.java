package com.avmerkez.reviewservice.controller;

import com.avmerkez.reviewservice.dto.CreateReviewRequest;
import com.avmerkez.reviewservice.dto.ReviewDto;
import com.avmerkez.reviewservice.entity.ApprovalStatus;
import com.avmerkez.reviewservice.security.UserDetailsImpl;
import com.avmerkez.reviewservice.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private UserDetailsImpl userDetails;
    private CreateReviewRequest createReviewRequest;
    private ReviewDto reviewDto;

    @BeforeEach
    void setUp() {
        userDetails = new UserDetailsImpl(
                1L,
                "keremguran",
                "kerem.guran@example.com",
                "hashedPassword",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        createReviewRequest = new CreateReviewRequest();
        createReviewRequest.setMallId(10L);
        createReviewRequest.setRating(4);
        createReviewRequest.setComment("Geniş otoparkı ve çocuk oyun alanı ile aileler için çok uygun bir AVM.");

        reviewDto = ReviewDto.builder()
                .id(100L)
                .userId(1L)
                .mallId(10L)
                .rating(4)
                .comment("Geniş otoparkı ve çocuk oyun alanı ile aileler için çok uygun bir AVM.")
                .createdAt(Instant.now())
                .approvalStatus(ApprovalStatus.PENDING)
                .build();
    }

    @Test
    void createReview_ShouldReturnCreatedStatus() {
        // Given
        when(reviewService.createReview(any(CreateReviewRequest.class), eq(1L))).thenReturn(reviewDto);

        // When
        ResponseEntity<?> response = reviewController.createReview(createReviewRequest, userDetails);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
} 