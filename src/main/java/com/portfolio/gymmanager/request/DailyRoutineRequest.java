package com.portfolio.gymmanager.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyRoutineRequest {
    private String title;
    private List<String> exerciseName;
    private List<Integer> exerciseLoad;
    private List<Integer> exerciseReps;
    private List<Integer> exerciseSets;
}
