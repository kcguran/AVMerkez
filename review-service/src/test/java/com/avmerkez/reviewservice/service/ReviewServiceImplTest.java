package com.avmerkez.reviewservice.service;

import com.avmerkez.reviewservice.client.MallServiceClient;
import com.avmerkez.reviewservice.client.StoreServiceClient;
import com.avmerkez.reviewservice.dto.CreateReviewRequest;
import com.avmerkez.reviewservice.dto.PagedResponse;
import com.avmerkez.reviewservice.dto.ReviewDto;
import com.avmerkez.reviewservice.dto.UpdateReviewStatusRequest;
import com.avmerkez.reviewservice.entity.ApprovalStatus;
import com.avmerkez.reviewservice.entity.Review;
import com.avmerkez.reviewservice.exception.InvalidInputException;
import com.avmerkez.reviewservice.exception.ResourceNotFoundException;
import com.avmerkez.reviewservice.mapper.ReviewMapper;
import com.avmerkez.reviewservice.repository.ReviewRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.ConstraintViolationException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private MallServiceClient mallServiceClient;
    @Mock
    private StoreServiceClient storeServiceClient;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private CreateReviewRequest createReviewRequestMall;
    private CreateReviewRequest createReviewRequestStore;
    private Review review;
    private ReviewDto reviewDto;
    private Long userId = 1L;
    private Long mallId = 10L;
    private Long storeId = 20L;
    private Long reviewId = 100L;
    private Long adminId = 99L;
    private int defaultPage = 0;
    private int defaultSize = 10;

    @BeforeEach
    void setUp() {
        createReviewRequestMall = CreateReviewRequest.builder()
                .mallId(mallId)
                .rating(4)
                .comment("Good mall")
                .build();

        createReviewRequestStore = CreateReviewRequest.builder()
                .storeId(storeId)
                .rating(5)
                .comment("Excellent store")
                .build();

        review = new Review();
        review.setId(reviewId);
        review.setUserId(userId);
        review.setMallId(mallId); // Example for mall review
        review.setRating(4);
        review.setComment("Good mall");
        review.setCreatedAt(Instant.now());
        review.setApprovalStatus(ApprovalStatus.PENDING); // Default status
        review.setApprovedBy(null);
        review.setApprovedAt(null);
        review.setRejectionReason(null);


        reviewDto = ReviewDto.builder()
                .id(reviewId)
                .userId(userId)
                .mallId(mallId)
                .rating(4)
                .comment("Good mall")
                .createdAt(review.getCreatedAt())
                .approvalStatus(ApprovalStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Create Review for Mall - Success")
    void createReview_ForMall_ShouldSucceed() {
        // Given
        given(mallServiceClient.checkMallExists(mallId)).willReturn(ResponseEntity.ok().build());
        given(reviewMapper.toEntity(createReviewRequestMall, userId)).willReturn(review);
        given(reviewRepository.save(any(Review.class))).willReturn(review);
        given(reviewMapper.toDto(review)).willReturn(reviewDto);

        // When
        ReviewDto result = reviewService.createReview(createReviewRequestMall, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMallId()).isEqualTo(mallId);
        assertThat(result.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING); // Check initial status
        verify(mallServiceClient, times(1)).checkMallExists(mallId);
        verify(storeServiceClient, never()).checkStoreExists(anyLong());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Create Review for Store - Success")
    void createReview_ForStore_ShouldSucceed() {
         // Given
         given(storeServiceClient.checkStoreExists(storeId)).willReturn(ResponseEntity.ok().build());
         review.setMallId(null);
         review.setStoreId(storeId);
         reviewDto.setMallId(null);
         reviewDto.setStoreId(storeId);
         reviewDto.setApprovalStatus(ApprovalStatus.PENDING);

         given(reviewMapper.toEntity(createReviewRequestStore, userId)).willReturn(review);
         given(reviewRepository.save(any(Review.class))).willReturn(review);
         given(reviewMapper.toDto(review)).willReturn(reviewDto);

         // When
         ReviewDto result = reviewService.createReview(createReviewRequestStore, userId);

         // Then
         assertThat(result).isNotNull();
         assertThat(result.getStoreId()).isEqualTo(storeId);
         assertThat(result.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);
         verify(storeServiceClient, times(1)).checkStoreExists(storeId);
         verify(mallServiceClient, never()).checkMallExists(anyLong());
         verify(reviewRepository, times(1)).save(any(Review.class));
    }


    @Test
    @DisplayName("Create Review for Mall - Mall Not Found")
    void createReview_ForMall_WhenMallNotFound_ShouldThrowInvalidInputException() {
        // Given
        Request dummyRequest = Request.create(Request.HttpMethod.HEAD, "/api/v1/malls/"+mallId+"/exists", Collections.emptyMap(), null, new RequestTemplate());
        given(mallServiceClient.checkMallExists(mallId)).willThrow(new FeignException.NotFound("Not Found", dummyRequest, null, null));

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(createReviewRequestMall, userId))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Geçersiz AVM ID: " + mallId);

        verify(mallServiceClient, times(1)).checkMallExists(mallId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

     @Test
     @DisplayName("Create Review for Store - Store Not Found")
     void createReview_ForStore_WhenStoreNotFound_ShouldThrowInvalidInputException() {
         // Given
         Request dummyRequest = Request.create(Request.HttpMethod.HEAD, "/api/v1/stores/"+storeId+"/exists", Collections.emptyMap(), null, new RequestTemplate());
         given(storeServiceClient.checkStoreExists(storeId)).willThrow(new FeignException.NotFound("Not Found", dummyRequest, null, null));

         // When & Then
         assertThatThrownBy(() -> reviewService.createReview(createReviewRequestStore, userId))
                 .isInstanceOf(InvalidInputException.class)
                 .hasMessageContaining("Geçersiz Mağaza ID: " + storeId);

         verify(storeServiceClient, times(1)).checkStoreExists(storeId);
         verify(reviewRepository, never()).save(any(Review.class));
     }

    @Test
    @DisplayName("Get Review By Id - Found")
    void getReviewById_WhenFound_ShouldReturnDto() {
        // Given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(reviewMapper.toDto(review)).willReturn(reviewDto);

        // When
        ReviewDto result = reviewService.getReviewById(reviewId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reviewId);
        verify(reviewRepository, times(1)).findById(reviewId); // Corrected verification
    }

    @Test
    @DisplayName("Get Review By Id - Not Found")
    void getReviewById_WhenNotFound_ShouldThrowResourceNotFoundException() {
        // Given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.getReviewById(reviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Yorum bulunamadı: id = " + reviewId);

        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("Get Reviews By MallId - Success")
    void getReviewsByMallId_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(defaultPage, defaultSize);
        review.setApprovalStatus(ApprovalStatus.APPROVED);
        reviewDto.setApprovalStatus(ApprovalStatus.APPROVED);
        List<Review> reviews = Collections.singletonList(review);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());
        List<ReviewDto> reviewDtos = Collections.singletonList(reviewDto);

        given(reviewRepository.findByMallIdAndApprovalStatus(eq(mallId), eq(ApprovalStatus.APPROVED), any(Pageable.class))).willReturn(reviewPage);
        given(reviewMapper.toDto(reviews)).willReturn(reviewDtos);

        // Act
        PagedResponse<ReviewDto> result = reviewService.getReviewsByMallId(mallId, defaultPage, defaultSize);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(reviewId);
        assertThat(result.getPageNumber()).isEqualTo(defaultPage);
        assertThat(result.getPageSize()).isEqualTo(defaultSize);
        verify(reviewRepository, times(1)).findByMallIdAndApprovalStatus(eq(mallId), eq(ApprovalStatus.APPROVED), any(Pageable.class));
    }

    @Test
    @DisplayName("Get Reviews By StoreId - Success")
    void getReviewsByStoreId_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(defaultPage, defaultSize);
        review.setMallId(null);
        review.setStoreId(storeId);
        review.setApprovalStatus(ApprovalStatus.APPROVED);
        reviewDto.setMallId(null);
        reviewDto.setStoreId(storeId);
        reviewDto.setApprovalStatus(ApprovalStatus.APPROVED);
        List<Review> reviews = Collections.singletonList(review);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());
        List<ReviewDto> reviewDtos = Collections.singletonList(reviewDto);

        given(reviewRepository.findByStoreIdAndApprovalStatus(eq(storeId), eq(ApprovalStatus.APPROVED), any(Pageable.class))).willReturn(reviewPage);
        given(reviewMapper.toDto(reviews)).willReturn(reviewDtos);

        // Act
        PagedResponse<ReviewDto> result = reviewService.getReviewsByStoreId(storeId, defaultPage, defaultSize);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStoreId()).isEqualTo(storeId);
        verify(reviewRepository, times(1)).findByStoreIdAndApprovalStatus(eq(storeId), eq(ApprovalStatus.APPROVED), any(Pageable.class));
    }

    @Test
    @DisplayName("Get Reviews By UserId - Success")
    void getReviewsByUserId_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(defaultPage, defaultSize);
        review.setApprovalStatus(ApprovalStatus.APPROVED);
        reviewDto.setApprovalStatus(ApprovalStatus.APPROVED);
        List<Review> reviews = Collections.singletonList(review);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());
        List<ReviewDto> reviewDtos = Collections.singletonList(reviewDto);

        given(reviewRepository.findByUserIdAndApprovalStatus(eq(userId), eq(ApprovalStatus.APPROVED), any(Pageable.class))).willReturn(reviewPage);
        given(reviewMapper.toDto(reviews)).willReturn(reviewDtos);

        // Act
        PagedResponse<ReviewDto> result = reviewService.getReviewsByUserId(userId, defaultPage, defaultSize);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(userId);
        verify(reviewRepository, times(1)).findByUserIdAndApprovalStatus(eq(userId), eq(ApprovalStatus.APPROVED), any(Pageable.class));
    }

    @Test
    @DisplayName("Get Pending Reviews - Success")
    void getPendingReviews_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(defaultPage, defaultSize);
        // review is already PENDING in setup
        List<Review> reviews = Collections.singletonList(review);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());
        List<ReviewDto> reviewDtos = Collections.singletonList(reviewDto);

        given(reviewRepository.findByApprovalStatus(eq(ApprovalStatus.PENDING), any(Pageable.class))).willReturn(reviewPage);
        given(reviewMapper.toDto(reviews)).willReturn(reviewDtos);

        // Act
        PagedResponse<ReviewDto> result = reviewService.getPendingReviews(defaultPage, defaultSize);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);
        verify(reviewRepository, times(1)).findByApprovalStatus(eq(ApprovalStatus.PENDING), any(Pageable.class));
    }

    @Test
    @DisplayName("Update Review Status to APPROVED - Success")
    void updateReviewStatus_Approve_Success() {
        // Arrange
        UpdateReviewStatusRequest updateRequest = UpdateReviewStatusRequest.builder()
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();
        // review starts as PENDING
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        // Capture the argument passed to save
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        given(reviewRepository.save(reviewCaptor.capture())).willAnswer(inv -> inv.getArgument(0)); // Return the saved entity

        // Create a DTO representing the expected *after* state
        ReviewDto approvedDto = ReviewDto.builder()
                .id(reviewId)
                .userId(userId)
                .mallId(mallId)
                .rating(4)
                .comment("Good mall")
                .createdAt(review.getCreatedAt())
                .approvalStatus(ApprovalStatus.APPROVED) // Expected status
                // updatedAt will be set by PreUpdate
                .build();
        given(reviewMapper.toDto(any(Review.class))).willReturn(approvedDto); // Assume mapper returns the correct DTO

        // Act
        ReviewDto result = reviewService.updateReviewStatus(reviewId, updateRequest, adminId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reviewId);
        assertThat(result.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED); // Check DTO status

        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(any(Review.class));

        // Verify the entity state *before* save
        Review savedReview = reviewCaptor.getValue();
        assertThat(savedReview.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
        assertThat(savedReview.getApprovedBy()).isEqualTo(adminId);
        assertThat(savedReview.getApprovedAt()).isNotNull();
        assertThat(savedReview.getRejectionReason()).isNull();
    }

    @Test
    @DisplayName("Update Review Status to REJECTED - Success")
    void updateReviewStatus_Reject_Success() {
        // Arrange
        String rejectionReason = "Inappropriate comment";
        UpdateReviewStatusRequest updateRequest = UpdateReviewStatusRequest.builder()
                .approvalStatus(ApprovalStatus.REJECTED)
                .rejectionReason(rejectionReason)
                .build();
        // review starts as PENDING
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        given(reviewRepository.save(reviewCaptor.capture())).willAnswer(inv -> inv.getArgument(0));

        ReviewDto rejectedDto = ReviewDto.builder()
                .id(reviewId)
                .approvalStatus(ApprovalStatus.REJECTED)
                .rejectionReason(rejectionReason)
                // ... other fields
                .build();
        given(reviewMapper.toDto(any(Review.class))).willReturn(rejectedDto);

        // Act
        ReviewDto result = reviewService.updateReviewStatus(reviewId, updateRequest, adminId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reviewId);
        assertThat(result.getApprovalStatus()).isEqualTo(ApprovalStatus.REJECTED);
        assertThat(result.getRejectionReason()).isEqualTo(rejectionReason);

        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(any(Review.class));

        Review savedReview = reviewCaptor.getValue();
        assertThat(savedReview.getApprovalStatus()).isEqualTo(ApprovalStatus.REJECTED);
        assertThat(savedReview.getApprovedBy()).isEqualTo(adminId);
        assertThat(savedReview.getApprovedAt()).isNotNull(); // We set timestamp even for rejection
        assertThat(savedReview.getRejectionReason()).isEqualTo(rejectionReason);
    }

    @Test
    @DisplayName("Update Review Status - Not Found")
    void updateReviewStatus_NotFound() {
        // Arrange
        UpdateReviewStatusRequest updateRequest = UpdateReviewStatusRequest.builder()
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reviewService.updateReviewStatus(reviewId, updateRequest, adminId))
            .isInstanceOf(ResourceNotFoundException.class);
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Delete Review - Success")
    void deleteReview_Success() {
        // Given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        willDoNothing().given(reviewRepository).delete(review);

        // When
        reviewService.deleteReview(reviewId);

        // Then
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    @DisplayName("Delete Review - Not Found")
    void deleteReview_NotFound_ShouldThrowResourceNotFoundException() {
        // Given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewId))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    @DisplayName("Create Review - Failure - Both Mall and Store ID Provided")
    void createReview_Failure_BothMallAndStoreIdProvided() {
        // Arrange
        // Create a request with both IDs set
        CreateReviewRequest invalidRequest = CreateReviewRequest.builder()
            .mallId(mallId)
            .storeId(storeId) // Set both
            .rating(3)
            .comment("Invalid review")
            .build();

        // Act & Assert
        // Service layer should throw InvalidInputException if both are provided
        assertThatThrownBy(() -> reviewService.createReview(invalidRequest, userId))
            .isInstanceOf(InvalidInputException.class) // Expect InvalidInputException
            .hasMessageContaining("Yorum için AVM veya Mağaza ID'lerinden yalnızca biri belirtilmelidir."); // Adjust expected message if needed

        // Verify no interaction with clients or repository if service validation fails first
        verify(mallServiceClient, never()).checkMallExists(anyLong());
        verify(storeServiceClient, never()).checkStoreExists(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Create Review - Failure - Neither Mall Nor Store ID Provided")
    void createReview_Failure_NeitherMallNorStoreIdProvided() {
        // Arrange
        CreateReviewRequest invalidRequest = CreateReviewRequest.builder()
            .mallId(null) // Set neither
            .storeId(null)
            .rating(3)
            .comment("Invalid review")
            .build();

        // Act & Assert
        // validateTargetExistence should throw InvalidInputException
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> reviewService.createReview(invalidRequest, userId));
        assertEquals("Yorum için AVM veya Mağaza ID belirtilmelidir.", exception.getMessage());
        verify(mallServiceClient, never()).checkMallExists(anyLong());
        verify(storeServiceClient, never()).checkStoreExists(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }
} 