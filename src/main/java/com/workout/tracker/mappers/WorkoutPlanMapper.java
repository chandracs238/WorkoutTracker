package com.workout.tracker.mappers;

import com.workout.tracker.dto.WorkoutPlanRequestDTO;
import com.workout.tracker.dto.WorkoutPlanResponseDTO;
import com.workout.tracker.dto.WorkoutPlanSummaryDTO;
import com.workout.tracker.model.WorkoutPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkoutPlanMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "exerciseList", ignore = true)
    WorkoutPlan toEntity(WorkoutPlanRequestDTO dto);

    WorkoutPlanResponseDTO toResponse(WorkoutPlan entity);

    WorkoutPlanSummaryDTO toSummary(WorkoutPlan entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "exerciseList", ignore = true)
    WorkoutPlan updateFromDto(@MappingTarget WorkoutPlan existing, WorkoutPlanRequestDTO dto);
}
