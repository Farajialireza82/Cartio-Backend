package com.cromulent.cartio.service;

import com.cromulent.cartio.dto.AuthenticationRequest;
import com.cromulent.cartio.dto.AuthenticationResponse;
import com.cromulent.cartio.dto.RegisterRequest;
import com.cromulent.cartio.model.User;
import com.cromulent.cartio.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        var user = new User();
        user.setUsername(request.username());
        user.setFullName(request.fullName());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            java.util.Collections.emptyList()
        ));

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        var user = userRepository.findByUsername(request.username())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            java.util.Collections.emptyList()
        ));

        return new AuthenticationResponse(jwtToken);
    }
}