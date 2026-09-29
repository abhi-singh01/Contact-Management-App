package com.nextgen.contactmanager.Service;

import com.nextgen.contactmanager.DTO.MyUserDTO;
import com.nextgen.contactmanager.DTO.PasswordChangeRequest;
import com.nextgen.contactmanager.DTO.RegisterRequest;
import com.nextgen.contactmanager.DTO.UserProfileUpdateRequest;
import com.nextgen.contactmanager.Exception.UserNotFoundException;
import com.nextgen.contactmanager.Model.MyUser;
import com.nextgen.contactmanager.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public MyUser getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public MyUserDTO getProfile(String email) {
        MyUser user = getByEmail(email);
        return new MyUserDTO(user.getFirstname(), user.getLastname(), user.getEmail(), null);
    }

    @Transactional
    public MyUserDTO register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required");
        if (request.getPassword() == null || request.getPassword().length() < 6)
            throw new IllegalArgumentException("Password must contain at least 6 characters");
        if (userRepository.existsById(request.getEmail().trim()))
            throw new IllegalArgumentException("An account with this email already exists");

        MyUser user = new MyUser();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setEnabled(true);
        userRepository.save(user);
        return getProfile(user.getEmail());
    }

    @Transactional
    public MyUserDTO updateProfile(String email, UserProfileUpdateRequest request) {
        MyUser user = getByEmail(email);
        if (request.getFirstname() != null && !request.getFirstname().isBlank())
            user.setFirstname(request.getFirstname().trim());
        if (request.getLastname() != null && !request.getLastname().isBlank())
            user.setLastname(request.getLastname().trim());
        userRepository.save(user);
        return getProfile(email);
    }

    @Transactional
    public void changePassword(String email, PasswordChangeRequest request) {
        MyUser user = getByEmail(email);
        if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new IllegalArgumentException("Current password is incorrect");
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6)
            throw new IllegalArgumentException("New password must contain at least 6 characters");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
