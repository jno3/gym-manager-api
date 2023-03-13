package com.portfolio.gymmanager.service;

import com.portfolio.gymmanager.model.DailyRoutine;
import com.portfolio.gymmanager.model.Gym;
import com.portfolio.gymmanager.repository.DailyRoutineRepository;
import com.portfolio.gymmanager.request.DailyRoutineRequest;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Data
public class DailyRoutineService {
    private final DailyRoutineRepository dailyRoutineRepository;
    public DailyRoutine saveDailyRoutine(DailyRoutineRequest dailyRoutineRequest, Gym gym){
        var dailyRoutine = DailyRoutine.builder()
                .title(dailyRoutineRequest.getTitle())
                .exerciseName(dailyRoutineRequest.getExerciseName())
                .exerciseReps(dailyRoutineRequest.getExerciseReps())
                .exerciseSets(dailyRoutineRequest.getExerciseSets())
                .gym(gym)
                .build();

        return dailyRoutineRepository.save(dailyRoutine);
    }

    public DailyRoutine getDailyRoutineById(Integer id) {
        Optional<DailyRoutine> dailyRoutine = dailyRoutineRepository.findById(id);
        if(dailyRoutine.isEmpty()){
            throw new NoSuchElementException();
        }
        return dailyRoutine.get();
    }

    public void editDailyRoutine(DailyRoutineRequest request, DailyRoutine dailyRoutine) {
        if(request.getTitle() != null){
            dailyRoutine.setTitle(request.getTitle());
        }
        if(request.getExerciseName() != null){
            dailyRoutine.setExerciseName(request.getExerciseName());
        }
        if(request.getExerciseSets() != null){
            dailyRoutine.setExerciseSets(request.getExerciseSets());
        }
        if(request.getExerciseReps() != null){
            dailyRoutine.setExerciseReps(request.getExerciseReps());
        }
        dailyRoutineRepository.save(dailyRoutine);
    }

    public void deleteDailyRoutine(DailyRoutine dailyRoutine) {
        dailyRoutineRepository.delete(dailyRoutine);
    }
}
