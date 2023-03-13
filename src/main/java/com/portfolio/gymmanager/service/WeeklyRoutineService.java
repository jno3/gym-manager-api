package com.portfolio.gymmanager.service;

import com.portfolio.gymmanager.model.Gym;
import com.portfolio.gymmanager.model.WeeklyRoutine;
import com.portfolio.gymmanager.repository.WeeklyRoutineRepository;
import com.portfolio.gymmanager.request.WeeklyRoutineRequest;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Data
@Service
public class WeeklyRoutineService {
    private final WeeklyRoutineRepository weeklyRoutineRepository;

    public WeeklyRoutine saveWeeklyRoutineRequest(WeeklyRoutineRequest weeklyRoutineRequest, Gym gym){
        var weeklyRoutine = WeeklyRoutine.builder()
                .title(weeklyRoutineRequest.getTitle())
                .gym(gym)
                .build();

        return weeklyRoutineRepository.save(weeklyRoutine);
    }

    public WeeklyRoutine saveWeeklyRoutine(WeeklyRoutine weeklyRoutine){
        return weeklyRoutineRepository.save(weeklyRoutine);
    }

    public WeeklyRoutine getWeeklyRoutineById(Integer id) {
        Optional<WeeklyRoutine> weeklyRoutine = weeklyRoutineRepository.findById(id);
        if(weeklyRoutine.isEmpty()){
            throw new NoSuchElementException();
        }
        return weeklyRoutine.get();
    }

    public void editWeeklyRoutine(WeeklyRoutineRequest request, WeeklyRoutine weeklyRoutine) {
        weeklyRoutine.setTitle(request.getTitle());
        weeklyRoutineRepository.save(weeklyRoutine);
    }

    public void deleteWeeklyRoutine(WeeklyRoutine weeklyRoutine) {
        weeklyRoutineRepository.delete(weeklyRoutine);
    }
}
