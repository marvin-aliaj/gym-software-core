package com.spring.jdbc.gym.management.dao;

import com.spring.jdbc.gym.management.model.Exercise;
import com.spring.jdbc.gym.management.model.ExerciseProgress;
import com.spring.jdbc.gym.management.model.PrescribedSet;
import com.spring.jdbc.gym.management.model.TrainingPlanExercise;
import com.spring.jdbc.gym.management.model.filter.ExerciseFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExerciseDao {
    private final JdbcTemplate jdbcTemplate;
    private final PrescribedSetDao prescribedSetDao;
    private final ExerciseProgressDao exerciseProgressDao;

    public ExerciseDao(
            JdbcTemplate jdbcTemplate,
            PrescribedSetDao prescribedSetDao,
            ExerciseProgressDao exerciseProgressDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.prescribedSetDao = prescribedSetDao;
        this.exerciseProgressDao = exerciseProgressDao;
    }

    public List<Exercise> getExerciseList(ExerciseFilter filter) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT exercises.* FROM exercises ");
        
        if (filter.hasTrainingPlanIdSet()) {
            sql.append("LEFT JOIN training_plan_exercises ON exercises.id = training_plan_exercises.exercise_id ");
        }
        
        sql.append("WHERE 1=1 ");
        
        if (filter.hasEntityId()) {
            sql.append("AND exercises.id = ? ");
            params.add(filter.getEntityId());
        }
        
        if (filter.hasTrainingPlanIdSet()) {
            sql.append("AND training_plan_exercises.training_plan_id = ? ");
            params.add(filter.getTrainingPlanId());
        }
        
        sql.append("LIMIT ? OFFSET ? ");
        params.add(filter.getLimit());
        params.add(filter.getOffset());

        String string_sql = sql.toString();
        try {
            return jdbcTemplate.query(string_sql, (rs, rowNum) -> {
                Exercise exercise = new Exercise();
                exercise.setId(rs.getString("id"));
                exercise.setTitle(rs.getString("title"));
                exercise.setDescription(rs.getString("description"));
                exercise.setCDate(rs.getString("cDate"));
                exercise.setMDate(rs.getString("mDate"));
                return exercise;
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Exercise getExerciseById(String id) throws Exception {
        String sql = "SELECT * FROM exercises WHERE id = ?::uuid";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Exercise exercise = new Exercise();
                exercise.setId(rs.getString("id"));
                exercise.setTitle(rs.getString("title"));
                exercise.setDescription(rs.getString("description"));
                exercise.setCDate(rs.getString("cDate"));
                exercise.setMDate(rs.getString("mDate"));
                return exercise;
            }, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Exercise createExercise(Exercise exercise) throws Exception {
        String sql = "INSERT INTO exercises(title, description, mDate, cDate) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, exercise.getTitle());
                ps.setString(2, exercise.getDescription());
                ps.setObject(3, Timestamp.valueOf(LocalDateTime.now()));
                ps.setObject(4, Timestamp.valueOf(LocalDateTime.now()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                exercise.setId(keyHolder.getKeys().get("id").toString());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return exercise;
    }

    public void updateExercise(Exercise exercise) throws Exception {
        String sql = "UPDATE exercises SET title = ?, description = ?, mDate = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql, exercise.getTitle(), exercise.getDescription(),
                    Timestamp.valueOf(LocalDateTime.now()), exercise.getId());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteExercise(String id) throws Exception {
        String sql = "DELETE FROM exercises WHERE id = ?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // ===== Training Plan Exercise Methods =====

    public TrainingPlanExercise getTrainingPlanExerciseById(String id, String userId) throws Exception {
        String sql = "SELECT * FROM training_plan_exercises WHERE id = ?::uuid";
        
        try {
            // get the training plan exercise
            TrainingPlanExercise tpe = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                TrainingPlanExercise exercise = new TrainingPlanExercise();
                exercise.setId(rs.getString("id"));
                exercise.setTrainingPlanId(rs.getString("training_plan_id"));
                exercise.setExerciseId(rs.getString("exercise_id"));
                exercise.setExerciseOrder(rs.getInt("exercise_order"));
                exercise.setNotes(rs.getString("notes"));
                exercise.setCDate(rs.getString("cDate"));
                exercise.setMDate(rs.getString("mDate"));
                return exercise;
            }, id);

            if (tpe != null) {
                // pastaj get exercise details
                Exercise exercise = getExerciseById(tpe.getExerciseId());
                tpe.setExercise(exercise);
                
                // get prescribed sets
                List<PrescribedSet> prescribedSets = prescribedSetDao.getPrescribedSetsByTrainingPlanExerciseId(id);
                tpe.setPrescribedSets(prescribedSets);
                
                // get recent progress (20 tfundit)
                List<ExerciseProgress> recentProgress = exerciseProgressDao.getRecentProgressByUserAndExercise(
                    userId,
                    tpe.getExerciseId(), 
                    20
                );
                tpe.setRecentProgress(recentProgress);
            }

            return tpe;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<TrainingPlanExercise> getTrainingPlanExercisesByTrainingPlanId(String trainingPlanId, String userId) throws Exception {
        String sql = "SELECT * FROM training_plan_exercises " +
                     "WHERE training_plan_id = ?::uuid " +
                     "ORDER BY exercise_order ASC";
        
        try {
            List<TrainingPlanExercise> exercises = jdbcTemplate.query(sql, (rs, rowNum) -> {
                TrainingPlanExercise exercise = new TrainingPlanExercise();
                exercise.setId(rs.getString("id"));
                exercise.setTrainingPlanId(rs.getString("training_plan_id"));
                exercise.setExerciseId(rs.getString("exercise_id"));
                exercise.setExerciseOrder(rs.getInt("exercise_order"));
                exercise.setNotes(rs.getString("notes"));
                exercise.setCDate(rs.getString("cDate"));
                exercise.setMDate(rs.getString("mDate"));
                return exercise;
            }, trainingPlanId);

            for (TrainingPlanExercise tpe : exercises) {
                // get exercise details
                Exercise exercise = getExerciseById(tpe.getExerciseId());
                tpe.setExercise(exercise);
                
                // get prescribed sets
                List<PrescribedSet> prescribedSets = prescribedSetDao.getPrescribedSetsByTrainingPlanExerciseId(tpe.getId());
                tpe.setPrescribedSets(prescribedSets);
                
                // get recent progress (20 tfundit)
                List<ExerciseProgress> recentProgress = exerciseProgressDao.getRecentProgressByUserAndExercise(
                    userId,
                    tpe.getExerciseId(), 
                    20
                );
                tpe.setRecentProgress(recentProgress);
            }

            return exercises;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public TrainingPlanExercise createTrainingPlanExercise(TrainingPlanExercise trainingPlanExercise) throws Exception {
        String sql = "INSERT INTO training_plan_exercises(training_plan_id, exercise_id, exercise_order, notes, mDate, cDate) " +
                     "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setObject(1, trainingPlanExercise.getTrainingPlanId());
                ps.setObject(2, trainingPlanExercise.getExerciseId());
                ps.setInt(3, trainingPlanExercise.getExerciseOrder() != null ? trainingPlanExercise.getExerciseOrder() : 1);
                ps.setString(4, trainingPlanExercise.getNotes());
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                trainingPlanExercise.setId(keyHolder.getKeys().get("id").toString());
            }
            return trainingPlanExercise;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void updateTrainingPlanExercise(TrainingPlanExercise trainingPlanExercise) throws Exception {
        String sql = "UPDATE training_plan_exercises SET " +
                     "training_plan_id = ?::uuid, " +
                     "exercise_id = ?::uuid, " +
                     "exercise_order = ?, " +
                     "notes = ?, " +
                     "mDate = ? " +
                     "WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql,
                    trainingPlanExercise.getTrainingPlanId(),
                    trainingPlanExercise.getExerciseId(),
                    trainingPlanExercise.getExerciseOrder(),
                    trainingPlanExercise.getNotes(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    trainingPlanExercise.getId());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteTrainingPlanExercise(String id) throws Exception {
        String sql = "DELETE FROM training_plan_exercises WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
