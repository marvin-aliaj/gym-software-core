package com.spring.jdbc.gym.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExerciseProgress {
    private String id;
    private String userId; // Added: WHO did the workout
    private String exerciseId;
    private String trainingPlanId; // Now optional: which plan they were following
    private Integer setNumber; // Renamed from 'set'
    private Integer reps;
    private BigDecimal weight;
    private String workoutDate; // Added: WHEN they did it
    private String notes; // Added: workout notes
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
