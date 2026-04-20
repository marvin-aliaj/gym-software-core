package com.spring.jdbc.gym.management.dao;

import com.spring.jdbc.gym.management.model.TrainingPlan;
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
public class TrainingPlanDao {
    private final JdbcTemplate jdbcTemplate;

    public TrainingPlanDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TrainingPlan> getTrainingPlanList(ExerciseFilter filter) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT tp.* FROM training_plans tp " +
                "LEFT JOIN user_training_plans utp ON tp.id = utp.training_plan_id " +
                "WHERE 1=1 ");
        
        if (filter.hasEntityId()) {
            sql.append(" AND tp.id = ?::uuid ");
            params.add(filter.getEntityId());
        }
        
        if (filter.hasUserIdSet()) {
            sql.append(" AND utp.user_id = ?::uuid ");
            params.add(filter.getUserId());
        }

        if (filter.hasAssigneeIdSet()) {
            sql.append(" AND utp.assignee_id = ?::uuid ");
            params.add(filter.getAssigneeId());
        }
        
        sql.append(" LIMIT ? OFFSET ? ");
        params.add(filter.getLimit());
        params.add(filter.getOffset());
        
        try {
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
                TrainingPlan trainingPlan = new TrainingPlan();
                trainingPlan.setId(rs.getString("id"));
                trainingPlan.setTitle(rs.getString("title"));
                trainingPlan.setDescription(rs.getString("description"));
                trainingPlan.setCDate(rs.getString("cDate"));
                trainingPlan.setMDate(rs.getString("mDate"));
                return trainingPlan;
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public TrainingPlan getTrainingPlanById(String id) throws Exception {
        String sql = "SELECT * FROM training_plans WHERE id = ?::uuid";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                TrainingPlan trainingPlan = new TrainingPlan();
                trainingPlan.setId(rs.getString("id"));
                trainingPlan.setTitle(rs.getString("title"));
                trainingPlan.setDescription(rs.getString("description"));
                trainingPlan.setCDate(rs.getString("cDate"));
                trainingPlan.setMDate(rs.getString("mDate"));
                return trainingPlan;
            }, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public TrainingPlan createTrainingPlan(TrainingPlan trainingPlan) throws Exception {
        String sql = "INSERT INTO training_plans(title, description, mDate, cDate) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, trainingPlan.getTitle());
                ps.setString(2, trainingPlan.getDescription());
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                trainingPlan.setId(keyHolder.getKeys().get("id").toString());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return trainingPlan;
    }

    public void updateTrainingPlan(TrainingPlan trainingPlan) throws Exception {
        String sql = "UPDATE training_plans SET title = ?, description = ?, mDate = ? WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql, trainingPlan.getTitle(), trainingPlan.getDescription(),
                    Timestamp.valueOf(LocalDateTime.now()), trainingPlan.getId());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteTrainingPlan(String id) throws Exception {
        String sql = "DELETE FROM training_plans WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
