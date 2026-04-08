package com.spring.jdbc.gym.management.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseFilter extends Filter {
    private String trainingPlanId;
    private String userId;

    public ExerciseFilter(String entityId, int limit, int offset) {
        super(entityId, limit, offset);
    }
    public ExerciseFilter(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public boolean hasTrainingPlanIdSet() {
        return trainingPlanId != null && !trainingPlanId.isEmpty();
    }

    public boolean hasUserIdSet() {
        return userId != null && !userId.isEmpty();
    }
}
