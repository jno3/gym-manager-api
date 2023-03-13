package com.portfolio.gymmanager.service;

import com.portfolio.gymmanager.model.Client;
import com.portfolio.gymmanager.model.User;
import com.portfolio.gymmanager.repository.ClientRepository;
import com.portfolio.gymmanager.repository.TokenRepository;
import com.portfolio.gymmanager.repository.UserRepository;
import com.portfolio.gymmanager.request.RegisterRequest;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Data
public class ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    public Client getClientById(Integer id){
        Optional<Client> client = clientRepository.findById(id);

        if(client.isEmpty()){
            throw new NoSuchElementException();
        }

        return client.get();
    }

    public List<Client> getClientByGymId(Integer id){
        Optional<List<Client>> clientList = clientRepository.findByGym_id(id);
        if(clientList.isEmpty()){
            throw new NoSuchElementException();
        }
        return clientList.get();
    }

    public void editClient(RegisterRequest request, Client client){
        client.setName(request.getName());
        clientRepository.save(client);
    }

    public Client saveClient(Client client){
        return clientRepository.save(client);
    }

    public void deleteClient(Client client){
        tokenRepository.deleteAll(client.getUser().getTokens());
        userRepository.delete(client.getUser());
        clientRepository.delete(client);
    }

    public Client getClientByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new RuntimeException();
        }
        return user.get().getClient();
    }
}
