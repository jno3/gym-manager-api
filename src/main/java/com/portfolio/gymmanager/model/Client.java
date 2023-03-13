package com.portfolio.gymmanager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "client")
public class Client extends RepresentationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    @Size(min = 4, max = 32)
    @Column(nullable = false)
    @NotNull
    String name;
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private User user;
    @ManyToOne
    @JoinColumn(name = "weekly_routine_id", referencedColumnName = "id")
    private WeeklyRoutine weeklyRoutine;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "gym_id", referencedColumnName = "id")
    private Gym gym;
}
