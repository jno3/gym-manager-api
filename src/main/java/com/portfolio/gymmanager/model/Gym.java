package com.portfolio.gymmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gym")
public class Gym{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Size(min = 4, max = 32)
    @Column(nullable = false)
    @NotNull
    private String name;
    @Size(min = 4, max = 64)
    @Column(nullable = false)
    @NotNull
    private String address;
    @OneToOne(mappedBy = "gym", cascade = CascadeType.ALL)
    private User user;
    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
    private List<Client> clients;
    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
    private List<DailyRoutine> dailyRoutines;
    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
    private List<WeeklyRoutine> weeklyRoutines;
}
