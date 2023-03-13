package com.portfolio.gymmanager.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    //USER STUFF
    private String username;
    private String password;

    //GYM & CLIENT STUFF
    private String name;

    //GYM STUFF
    private String address;

    //CLIENT STUFF
    private String gymUsername;
}
