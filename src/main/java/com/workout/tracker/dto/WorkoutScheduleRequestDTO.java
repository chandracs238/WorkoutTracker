package com.workout.tracker.dto;

import com.workout.tracker.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutScheduleRequestDTO {
    @Schema(description = "Workout plan name")
    @NotNull(message = "Workout plan name must not be null")
    private String workoutPlanName;

    @Schema(description = "Schedule Workout")
    @NotNull(message = "Workout Plan must be schedule in future date")
    private LocalDateTime scheduledAt;

    @Schema(description = "Workout Schedule defalut pending")
    @NotNull(message = "Workout Schedule must have status")
    private Status status;

    @Schema(description = "Workout Schedule Completed Date")
    private LocalDateTime completedAt;

    @Schema(description = "Mins Trained you body")
    private Integer durationMins;

    @Schema(description = "Write what went good, what was hard")
    private String notes;
}
