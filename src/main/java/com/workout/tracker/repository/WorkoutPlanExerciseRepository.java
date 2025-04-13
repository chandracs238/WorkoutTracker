package com.workout.tracker.repository;

import com.workout.tracker.model.WorkoutPlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkoutPlanExerciseRepository extends JpaRepository<WorkoutPlanExercise, UUID> {
    List<WorkoutPlanExercise> getAllByWorkoutPlanId(UUID id);
}
