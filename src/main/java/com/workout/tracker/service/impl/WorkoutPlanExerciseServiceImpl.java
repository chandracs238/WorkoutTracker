package com.workout.tracker.service.impl;

import com.workout.tracker.dto.WorkoutPlanExerciseRequestDTO;
import com.workout.tracker.dto.WorkoutPlanExerciseResponseDTO;
import com.workout.tracker.dto.WorkoutPlanExerciseSummaryDTO;
import com.workout.tracker.exception.CustomAccessDeniedException;
import com.workout.tracker.exception.WorkoutPlanExerciseNotFoundException;
import com.workout.tracker.exception.WorkoutPlanNotFoundException;
import com.workout.tracker.mappers.WorkoutPlanExerciseMapper;
import com.workout.tracker.model.WorkoutPlan;
import com.workout.tracker.model.WorkoutPlanExercise;
import com.workout.tracker.repository.WorkoutPlanExerciseRepository;
import com.workout.tracker.repository.WorkoutPlanRepository;
import com.workout.tracker.service.WorkoutPlanExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutPlanExerciseServiceImpl implements WorkoutPlanExerciseService {

    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanExerciseMapper workoutPlanExerciseMapper;

    @Override
    public List<WorkoutPlanExerciseSummaryDTO> getAllWorkoutPlanExerciseByWorkoutPlanId(String username, UUID workoutPlanId) {
        WorkoutPlan workoutPlan = validateWorkoutPlanAccess(username, workoutPlanId);
        return workoutPlanExerciseRepository.getAllByWorkoutPlanId(workoutPlanId).stream()
                .map(workoutPlanExerciseMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WorkoutPlanExerciseResponseDTO getWorkoutPlanExerciseById(String username, UUID exerciseId, UUID planId) {
        WorkoutPlanExercise exercise = validateWorkoutPlanExerciseAccess(username, planId, exerciseId);
        return workoutPlanExerciseMapper.toResponseDTO(exercise);
    }

    @Override
    public WorkoutPlanExerciseResponseDTO createWorkoutPlanExercise(String username, WorkoutPlanExerciseRequestDTO requestDTO, UUID workoutPlanId) {
        WorkoutPlan workoutPlan = validateWorkoutPlanAccess(username, workoutPlanId);
        WorkoutPlanExercise exercise = workoutPlanExerciseMapper.toEntity(requestDTO);
        exercise.setWorkoutPlan(workoutPlan);
        WorkoutPlanExercise saved = workoutPlanExerciseRepository.save(exercise);
        return workoutPlanExerciseMapper.toResponseDTO(saved);
    }

    @Override
    public WorkoutPlanExerciseResponseDTO updateWorkoutPlanExercise(String username, UUID exerciseId, WorkoutPlanExerciseRequestDTO requestDTO, UUID workoutPlanId) {
        WorkoutPlanExercise exercise = validateWorkoutPlanExerciseAccess(username, workoutPlanId, exerciseId);
        WorkoutPlanExercise updated = workoutPlanExerciseMapper.updateFromDTO(exercise, requestDTO);
        WorkoutPlanExercise savedUpdate = workoutPlanExerciseRepository.save(updated);
        return workoutPlanExerciseMapper.toResponseDTO(savedUpdate);
    }

    @Override
    public void deleteWorkoutPlanExercise(String username, UUID exerciseId, UUID workoutPlanId) {
        WorkoutPlanExercise exercise = validateWorkoutPlanExerciseAccess(username, workoutPlanId, exerciseId);
        workoutPlanExerciseRepository.deleteById(exerciseId);
    }

    // ==== Private Helpers Methods ====

    private WorkoutPlan validateWorkoutPlanAccess(String username, UUID workoutPlanId) {
        WorkoutPlan workoutPlan = findWorkoutPlanById(workoutPlanId);
        checkOwnership(username, workoutPlan);
        return workoutPlan;
    }

    private WorkoutPlanExercise validateWorkoutPlanExerciseAccess(String username, UUID workoutPlanId, UUID exerciseId) {
        WorkoutPlan workoutPlan = validateWorkoutPlanAccess(username, workoutPlanId);
        WorkoutPlanExercise workoutPlanExercise = findWorkoutPlanExerciseById(exerciseId);
        if (!workoutPlanExercise.getWorkoutPlan().getId().equals(workoutPlanId)) {
            throw new CustomAccessDeniedException("WorkoutPlanExercise does not belong to the specified WorkoutPlan");
        }
        return workoutPlanExercise;
    }

    private void checkOwnership(String username, WorkoutPlan workoutPlan) {
        if (!workoutPlan.getUser().getUsername().equals(username)) {
            throw new CustomAccessDeniedException("User is not authorized to access this");
        }
    }

    private WorkoutPlanExercise findWorkoutPlanExerciseById(UUID id) {
        return workoutPlanExerciseRepository.findById(id)
                .orElseThrow(() -> new WorkoutPlanExerciseNotFoundException("WorkoutPlanExercise not found"));
    }

    private WorkoutPlan findWorkoutPlanById(UUID id) {
        return workoutPlanRepository.findById(id)
                .orElseThrow(() -> new WorkoutPlanNotFoundException("WorkoutPlan not found"));
    }
}
