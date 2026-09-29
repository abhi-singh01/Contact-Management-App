package com.nextgen.contactmanager.Controller;

import com.nextgen.contactmanager.DTO.MyUserDTO;
import com.nextgen.contactmanager.DTO.PasswordChangeRequest;
import com.nextgen.contactmanager.DTO.RegisterRequest;
import com.nextgen.contactmanager.DTO.UserProfileUpdateRequest;
import com.nextgen.contactmanager.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @GetMapping("/me")
    public ResponseEntity<MyUserDTO> me(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }

    @PostMapping("/register")
    public ResponseEntity<MyUserDTO> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PutMapping("/profile")
    public ResponseEntity<MyUserDTO> updateProfile(Authentication authentication,
                                                    @RequestBody UserProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), request));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                               @RequestBody PasswordChangeRequest request) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
