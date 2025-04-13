package com.workout.tracker.service.impl;

import com.workout.tracker.dto.WorkoutScheduleRequestDTO;
import com.workout.tracker.dto.WorkoutScheduleResponseDTO;
import com.workout.tracker.dto.WorkoutScheduleSummaryDTO;
import com.workout.tracker.exception.CustomAccessDeniedException;
import com.workout.tracker.exception.UserNotFoundException;
import com.workout.tracker.exception.WorkoutScheduleNotFoundException;
import com.workout.tracker.mappers.WorkoutScheduleMapper;
import com.workout.tracker.model.User;
import com.workout.tracker.model.WorkoutPlan;
import com.workout.tracker.model.WorkoutSchedule;
import com.workout.tracker.repository.UserRepository;
import com.workout.tracker.repository.WorkoutScheduleRepository;
import com.workout.tracker.service.WorkoutScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutScheduleServiceImpl implements WorkoutScheduleService {

    private final WorkoutScheduleRepository workoutScheduleRepository;
    private final WorkoutScheduleMapper workoutScheduleMapper;
    private final UserRepository userRepository;

    @Override
    public List<WorkoutScheduleSummaryDTO> getAllUpcomingWorkoutSchedulesByUsername(String username) {
        return workoutScheduleRepository.findAllByUserUsernameAndScheduledAtAfter(username, LocalDateTime.now()).stream()
                .map(workoutScheduleMapper::toSummaryDTO).collect(Collectors.toList());
    }

    @Override
    public List<WorkoutScheduleSummaryDTO> getAllCompletedWorkoutSchedulesByUsername(String username) {
        return workoutScheduleRepository.findAllByUserUsernameAndScheduledAtBefore(username, LocalDateTime.now()).stream()
                .map(workoutScheduleMapper::toSummaryDTO).collect(Collectors.toList());
    }

    @Override
    public WorkoutScheduleResponseDTO getWorkoutScheduleById(String username, UUID workoutScheduleId) {
        WorkoutSchedule workoutSchedule = checkOwership(username, workoutScheduleId);
        return workoutScheduleMapper.toResponseDTO(workoutSchedule);
    }

    @Override
    public WorkoutScheduleResponseDTO createWorkoutSchedule(String username, WorkoutScheduleRequestDTO requestDTO) {
        WorkoutSchedule workoutSchedule = workoutScheduleMapper.toEntity(requestDTO);
        workoutSchedule.setUser(findUserByUsername(username));
        WorkoutSchedule saved = workoutScheduleRepository.save(workoutSchedule);
        return workoutScheduleMapper.toResponseDTO(saved);
    }

    @Override
    public WorkoutScheduleResponseDTO updateWorkoutSchedule(String username, UUID workoutScheduleId, WorkoutScheduleRequestDTO requestDTO) {
        WorkoutSchedule workoutSchedule = checkOwership(username, workoutScheduleId);
        WorkoutSchedule updated = workoutScheduleMapper.updateFromDTO(workoutSchedule, requestDTO);
        WorkoutSchedule savedUpdate = workoutScheduleRepository.save(updated);
        return workoutScheduleMapper.toResponseDTO(savedUpdate);
    }

    @Override
    public void deleteWorkoutSchedule(String username, UUID workoutScheduleId) {
        WorkoutSchedule workoutSchedule = checkOwership(username, workoutScheduleId);
        workoutScheduleRepository.deleteById(workoutScheduleId);
    }

    private WorkoutSchedule findWorkoutScheduleById(UUID id){
        return workoutScheduleRepository.findById(id)
                .orElseThrow(() -> new WorkoutScheduleNotFoundException("Workout schedule not found"));
    }

    private User findUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private WorkoutSchedule checkOwership(String username, UUID workoutScheduleId){
        WorkoutSchedule workoutSchedule = findWorkoutScheduleById(workoutScheduleId);
        if (!workoutSchedule.getUser().getUsername().equals(username)){
            throw new CustomAccessDeniedException("not authorized");
        }
        return workoutSchedule;
    }

}
