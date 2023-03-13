package com.portfolio.gymmanager.controller;


import com.portfolio.gymmanager.request.AuthenticationRequest;
import com.portfolio.gymmanager.security.AuthenticationResponse;
import com.portfolio.gymmanager.security.AuthenticationService;
import com.portfolio.gymmanager.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register_gym")
    public ResponseEntity<AuthenticationResponse> registerGym(
            @RequestBody RegisterRequest registerRequest
            ){
        return ResponseEntity.ok(authenticationService.registerGym(registerRequest));
    }

    @PreAuthorize("hasRole('GYM')")
    @PostMapping("/register_client")
    public ResponseEntity<AuthenticationResponse> registerClient(
            @RequestBody RegisterRequest registerRequest
    ){
        return ResponseEntity.ok(authenticationService.registerClient(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
            ){
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}
