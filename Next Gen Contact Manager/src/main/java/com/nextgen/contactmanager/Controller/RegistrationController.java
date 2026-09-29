package com.nextgen.contactmanager.Controller;

import com.nextgen.contactmanager.DTO.RegisterRequest;
import com.nextgen.contactmanager.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    // Kept for compatibility with the existing JSON registration flow.
    @PostMapping("/signup/user")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    // Used by the existing Thymeleaf signup form.
    @PostMapping("/register/user")
    public RedirectView newUser(RegisterRequest request) {
        userService.register(request);
        return new RedirectView("/login");
    }
}
