package com.portfolio.gymmanager.service;

import com.portfolio.gymmanager.model.Gym;
import com.portfolio.gymmanager.model.User;
import com.portfolio.gymmanager.repository.GymRepository;
import com.portfolio.gymmanager.repository.TokenRepository;
import com.portfolio.gymmanager.repository.UserRepository;
import com.portfolio.gymmanager.request.RegisterRequest;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Data
public class GymService {
    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public Gym getGymByUsername(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new RuntimeException();
        }
        return user.get().getGym();
    }

    public void saveGym(Gym gym) {
        gymRepository.save(gym);
    }

    public void deleteGym(Gym gym) {
        tokenRepository.deleteAll(gym.getUser().getTokens());
        userRepository.delete(gym.getUser());
        gymRepository.delete(gym);
    }

    public void editGym(RegisterRequest request, Gym gym) {
        if(request.getName() != null){
            gym.setName(request.getName());
        }
        if(request.getAddress() != null){
            gym.setAddress(request.getAddress());
        }
        gymRepository.save(gym);
    }
}
