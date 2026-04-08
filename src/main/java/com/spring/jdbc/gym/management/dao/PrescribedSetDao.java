package com.spring.jdbc.gym.management.dao;

import com.spring.jdbc.gym.management.model.PrescribedSet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PrescribedSetDao {
    private final JdbcTemplate jdbcTemplate;

    public PrescribedSetDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PrescribedSet> getPrescribedSetsByTrainingPlanExerciseId(String trainingPlanExerciseId) throws Exception {
        String sql = "SELECT * FROM training_plan_exercise_sets " +
                     "WHERE training_plan_exercise_id = ?::uuid " +
                     "ORDER BY set_number ASC";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                PrescribedSet set = new PrescribedSet();
                set.setId(rs.getString("id"));
                set.setTrainingPlanExerciseId(rs.getString("training_plan_exercise_id"));
                set.setSetNumber(rs.getInt("set_number"));
                set.setPrescribedReps(rs.getInt("prescribed_reps"));
                set.setPrescribedWeight(rs.getBigDecimal("prescribed_weight"));
                set.setRestSeconds((Integer) rs.getObject("rest_seconds"));
                set.setNotes(rs.getString("notes"));
                set.setCDate(rs.getString("cDate"));
                set.setMDate(rs.getString("mDate"));
                return set;
            }, trainingPlanExerciseId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
