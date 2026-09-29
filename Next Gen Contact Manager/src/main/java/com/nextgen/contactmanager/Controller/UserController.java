package com.nextgen.contactmanager.Controller;

import com.nextgen.contactmanager.DTO.MyUserDTO;
import com.nextgen.contactmanager.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/details")
    public ResponseEntity<MyUserDTO> getUserDetails(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }
}
