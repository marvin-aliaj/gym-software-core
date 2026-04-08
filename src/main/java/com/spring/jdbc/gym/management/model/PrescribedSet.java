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
public class PrescribedSet {
    private String id;
    private String trainingPlanExerciseId;
    private Integer setNumber;
    private Integer prescribedReps;
    private BigDecimal prescribedWeight;
    private Integer restSeconds;
    private String notes;
    @JsonProperty
    private String cDate;
    @JsonProperty
    private String mDate;
}
