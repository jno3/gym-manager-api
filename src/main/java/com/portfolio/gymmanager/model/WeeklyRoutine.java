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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "weekly_routine")
public class WeeklyRoutine extends RepresentationModel<WeeklyRoutine> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Size(min = 4, max = 128)
    @Column(nullable = false)
    @NotNull
    private String title;
    @ManyToMany
    @JoinTable(
            name = "routine_exercises",
            joinColumns = @JoinColumn(name = "daily_routine_collection_id"),
            inverseJoinColumns = @JoinColumn(name = "daily_routine_id")
    )
    private List<DailyRoutine> dailyRoutines;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "gym_id", referencedColumnName = "id")
    private Gym gym;
    @OneToMany(mappedBy = "weeklyRoutine", cascade = CascadeType.ALL  )
    @JsonBackReference
    private List<Client> clients;
}
