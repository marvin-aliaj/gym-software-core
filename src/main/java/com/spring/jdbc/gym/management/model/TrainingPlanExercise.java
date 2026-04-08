package com.spring.jdbc.gym.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainingPlanExercise {
    private String id;
    private String trainingPlanId;
    private String exerciseId;
    private Integer exerciseOrder;
    private String notes;
    
    // Nested data
    private Exercise exercise; // Exercise details
    private List<PrescribedSet> prescribedSets; // What client SHOULD do
    private List<ExerciseProgress> recentProgress; // What client DID (last N workouts)
    
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
