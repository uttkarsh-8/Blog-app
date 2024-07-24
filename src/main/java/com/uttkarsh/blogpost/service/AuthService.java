package com.uttkarsh.blogpost.service;

import com.uttkarsh.blogpost.dto.AuthenticationResponse;
import com.uttkarsh.blogpost.dto.UserDto;
import com.uttkarsh.blogpost.model.Role;
import com.uttkarsh.blogpost.model.User;
import com.uttkarsh.blogpost.repository.UserRepository;
import com.uttkarsh.blogpost.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));

        User savedUser = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(savedUser);
        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse authenticate(UserDto userDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
        );

        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtTokenProvider.generateToken(user);
        return AuthenticationResponse.builder().token(token).build();
    }
}