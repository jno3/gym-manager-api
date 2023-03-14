package com.portfolio.gymmanager.controller;


import com.portfolio.gymmanager.model.Client;
import com.portfolio.gymmanager.model.Gym;
import com.portfolio.gymmanager.request.RegisterRequest;
import com.portfolio.gymmanager.response.DeleteResponse;
import com.portfolio.gymmanager.security.JwtService;
import com.portfolio.gymmanager.service.ClientService;
import lombok.Data;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@PreAuthorize("hasRole('CLIENT')'")
@RequestMapping("/api/v1/client")
@Data
public class ClientController {
    private final ClientService clientService;
    private final JwtService jwtService;

    @GetMapping("/")
    public ResponseEntity<EntityModel<Client>> clientHomepage(
            @RequestHeader("Authorization") String bearer
    ) {
        String jwt = bearer.substring(7);
        String clientUsername = jwtService.extractUsername(jwt);
        Client client = clientService.getClientByUsername(clientUsername);
        return ResponseEntity.ok(EntityModel.of(client));
    }
    @DeleteMapping("/")
    public ResponseEntity<DeleteResponse> removeClient(
            @RequestHeader("Authorization") String bearer
    ) {
        String jwt = bearer.substring(7);
        Client client = clientService.getClientByUsername(jwtService.extractUsername(jwt));
        clientService.deleteClient(client);
        DeleteResponse deleteResponse = DeleteResponse.builder().
                message("Client successfully removed").
                build();
        return ResponseEntity.ok(deleteResponse);
    }

}
