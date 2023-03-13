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

import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "daily_routine")
public class DailyRoutine extends RepresentationModel<DailyRoutine> {
    @Id
    @GeneratedValue
    private Integer id;
    @Size(min = 4, max = 128)
    @Column(nullable = false)
    @NotNull
    private String title;
    @NotNull
    private List<String> exerciseName;
    @NotNull
    private List<Integer> exerciseReps;
    @NotNull
    private List<Integer> exerciseSets;
    @ManyToMany(mappedBy = "dailyRoutines")
    @JsonBackReference
    private List<WeeklyRoutine> weeklyRoutines;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "gym_id", referencedColumnName = "id")
    private Gym gym;

}
