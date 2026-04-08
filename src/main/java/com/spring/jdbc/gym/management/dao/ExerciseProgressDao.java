package com.spring.jdbc.gym.management.dao;

import com.spring.jdbc.gym.management.model.ExerciseProgress;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExerciseProgressDao {
    private final JdbcTemplate jdbcTemplate;

    public ExerciseProgressDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ExerciseProgress> getRecentProgressByUserAndExercise(
            String userId,
            String exerciseId,
            int limit) throws Exception {
        String sql = "SELECT * FROM exercise_progress " +
                     "WHERE user_id = ?::uuid " +
                     "AND exercise_id = ?::uuid " +
                     "ORDER BY workout_date DESC, cDate DESC " +
                     "LIMIT ?";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                ExerciseProgress progress = new ExerciseProgress();
                progress.setId(rs.getString("id"));
                progress.setUserId(rs.getString("user_id"));
                progress.setExerciseId(rs.getString("exercise_id"));
                progress.setTrainingPlanId(rs.getString("training_plan_id")); // Can be null
                progress.setSetNumber(rs.getInt("set_number"));
                progress.setReps(rs.getInt("reps"));
                progress.setWeight(rs.getBigDecimal("weight"));
                progress.setWorkoutDate(rs.getString("workout_date"));
                progress.setNotes(rs.getString("notes"));
                progress.setCDate(rs.getString("cDate"));
                progress.setMDate(rs.getString("mDate"));
                return progress;
            }, userId, exerciseId, limit);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // Keep old method for backward compatibility but update to use new schema
    @Deprecated
    public List<ExerciseProgress> getRecentProgressByExerciseAndTrainingPlan(
            String exerciseId, 
            String trainingPlanId, 
            int limit) throws Exception {
        String sql = "SELECT * FROM exercise_progress " +
                     "WHERE exercise_id = ?::uuid " +
                     "AND (training_plan_id = ?::uuid OR training_plan_id IS NULL) " +
                     "ORDER BY workout_date DESC, cDate DESC " +
                     "LIMIT ?";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                ExerciseProgress progress = new ExerciseProgress();
                progress.setId(rs.getString("id"));
                progress.setUserId(rs.getString("user_id"));
                progress.setExerciseId(rs.getString("exercise_id"));
                progress.setTrainingPlanId(rs.getString("training_plan_id"));
                progress.setSetNumber(rs.getInt("set_number"));
                progress.setReps(rs.getInt("reps"));
                progress.setWeight(rs.getBigDecimal("weight"));
                progress.setWorkoutDate(rs.getString("workout_date"));
                progress.setNotes(rs.getString("notes"));
                progress.setCDate(rs.getString("cDate"));
                progress.setMDate(rs.getString("mDate"));
                return progress;
            }, exerciseId, trainingPlanId, limit);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
