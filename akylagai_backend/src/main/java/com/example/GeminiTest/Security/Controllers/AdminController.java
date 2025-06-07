package com.example.GeminiTest.Security.Controllers;

import com.example.GeminiTest.Security.DTO.RegisterDto;
import com.example.GeminiTest.Security.Repositories.RoleRepository;
import com.example.GeminiTest.Security.Repositories.UserRepository;
import com.example.GeminiTest.Security.Role;
import com.example.GeminiTest.Security.UserPackage.UpdateUser;
import com.example.GeminiTest.Security.UserPackage.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/admin")
@Transactional
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/delete-user/{username}")
    @Transactional
    public ResponseEntity<String> deleteUser (@PathVariable String username) {
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User  not found"));

            user.getRoles().clear();

            userRepository.delete(user);
            return ResponseEntity.ok("User  deleted successfully!");
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/update-user/{username}")
    public ResponseEntity<String> updateUser  (@PathVariable String username, @RequestBody UpdateUser request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User  not found"));

        if (request.getNewUsername() != null) {
            user.setUsername(request.getNewUsername());
        }
        if (request.getNewPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
        return ResponseEntity.ok("User  updated successfully!");
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/add-user")
    public ResponseEntity<String> addUser (@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username is taken!");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        String roleName = registerDto.getRole();
        Role role;

        if (roleName != null && (roleName.equalsIgnoreCase("ADMIN") || roleName.equalsIgnoreCase("USER"))) {
            role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
        } else {
            return ResponseEntity.badRequest().body("Invalid role specified!");
        }

        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);
        return ResponseEntity.ok("User  created successfully!");
    }
}