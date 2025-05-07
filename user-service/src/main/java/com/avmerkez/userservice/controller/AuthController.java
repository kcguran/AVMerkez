package com.avmerkez.userservice.controller;

// import com.avmerkez.userservice.dto.MessageResponse; // Removed
// import com.avmerkez.userservice.dto.RegisterRequest; // Removed
// import com.avmerkez.userservice.exception.UserAlreadyExistsException; // Removed
import com.avmerkez.userservice.service.AuthService;
// import jakarta.validation.Valid; // Removed
import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus; // Removed
// import org.springframework.http.ResponseEntity; // Removed
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth") // Base path kept, but no endpoints currently
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService; // Kept for consistency, but service is empty now

    /* // Removed register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User registered successfully!"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse(e.getMessage()));
        }
         catch (Exception e) {
             return ResponseEntity
                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body(new MessageResponse("An unexpected error occurred during registration."));
        }
    }
    */

    // Future endpoints for user profile management etc. can be added here
} 