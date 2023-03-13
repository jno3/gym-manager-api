package com.portfolio.gymmanager.security;


import com.portfolio.gymmanager.model.Client;
import com.portfolio.gymmanager.model.Gym;
import com.portfolio.gymmanager.model.Token;
import com.portfolio.gymmanager.model.User;
import com.portfolio.gymmanager.repository.TokenRepository;
import com.portfolio.gymmanager.repository.UserRepository;
import com.portfolio.gymmanager.request.AuthenticationRequest;
import com.portfolio.gymmanager.request.RegisterRequest;
import com.portfolio.gymmanager.role.TokenRole;
import com.portfolio.gymmanager.role.UserRole;
import com.portfolio.gymmanager.service.ClientService;
import com.portfolio.gymmanager.service.GymService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final GymService gymService;

    private final ClientService clientService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse registerGym(RegisterRequest request) {

        if (
                request.getPassword().length() < 4 ||
                        request.getPassword().length() > 32 ||
                        request.getUsername().length() > 32 ||
                request.getUsername().indexOf('@') != -1
        ) {
            request.setUsername("");
        }

        var gym = Gym.builder()
                .name(request.getName())
                .address(request.getAddress())
                .build();

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.GYM)
                .build();

        gym.setUser(user);
        user.setGym(gym);
        gymService.saveGym(gym);
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse registerClient(RegisterRequest request) {

        String gymUsername = request.getGymUsername();

        if (request.getPassword().length() < 4 || request.getPassword().length() > 32 || request.getUsername().length() > 32) {
            request.setUsername("");
        } else {
            String clientUsername = request.getUsername();
            request.setUsername(clientUsername.concat("@" + gymUsername));
        }

        var client = Client.builder()
                .name(request.getName())
                .build();

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CLIENT)
                .build();

        Gym gym = gymService.getGymByUsername(gymUsername);

        client.setGym(gym);
        client.setUser(user);
        user.setClient(client);


        var savedUser = userRepository.save(user);
        List<Client> clients = gym.getClients();
        clients.add(savedUser.getClient());
        gym.setClients(clients);
        gymService.saveGym(gym);

        var jwtToken = jwtService.generateToken(savedUser);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenRole(TokenRole.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
