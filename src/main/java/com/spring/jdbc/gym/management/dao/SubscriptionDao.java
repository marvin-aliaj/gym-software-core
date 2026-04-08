package com.spring.jdbc.gym.management.dao;

import com.spring.jdbc.gym.management.model.Subscription;
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
public class SubscriptionDao {
    private final JdbcTemplate jdbcTemplate;

    public SubscriptionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Subscription> getSubscriptionList(ExerciseFilter filter) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM subscriptions WHERE 1=1 ");
        
        if (filter.hasEntityId()) {
            sql.append(" AND id = ?::uuid ");
            params.add(filter.getEntityId());
        }
        
        if (filter.hasUserIdSet()) {
            sql.append(" AND user_id = ?::uuid ");
            params.add(filter.getUserId());
        }
        
        sql.append(" LIMIT ? OFFSET ? ");
        params.add(filter.getLimit());
        params.add(filter.getOffset());
        
        try {
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
                Subscription subscription = new Subscription();
                subscription.setId(rs.getString("id"));
                subscription.setStartDate(rs.getString("start_date"));
                subscription.setEndDate(rs.getString("end_date"));
                subscription.setActive(rs.getBoolean("is_active"));
                subscription.setCDate(rs.getString("cDate"));
                subscription.setMDate(rs.getString("mDate"));
                return subscription;
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Subscription getSubscriptionById(String id) throws Exception {
        String sql = "SELECT * FROM subscriptions WHERE id = ?::uuid";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Subscription subscription = new Subscription();
                subscription.setId(rs.getString("id"));
                subscription.setStartDate(rs.getString("start_date"));
                subscription.setEndDate(rs.getString("end_date"));
                subscription.setActive(rs.getBoolean("is_active"));
                subscription.setCDate(rs.getString("cDate"));
                subscription.setMDate(rs.getString("mDate"));
                return subscription;
            }, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Subscription createSubscription(Subscription subscription) throws Exception {
        String sql = "INSERT INTO subscriptions(user_id, plan_id, start_date, end_date, is_active, mDate, cDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, subscription.getUser().getId());
                ps.setString(2, subscription.getPlan().getId());
                ps.setString(3, subscription.getStartDate());
                ps.setString(4, subscription.getEndDate());
                ps.setBoolean(5, subscription.isActive());
                ps.setObject(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setObject(7, Timestamp.valueOf(LocalDateTime.now()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                subscription.setId(keyHolder.getKeys().get("id").toString());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return subscription;
    }

    public void updateSubscription(Subscription subscription) throws Exception {
        String sql = "UPDATE subscriptions SET user_id = ?, plan_id = ?, start_date = ?, end_date = ?, is_active = ?, mDate = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql, subscription.getUser().getId(), subscription.getPlan().getId(),
                    subscription.getStartDate(), subscription.getEndDate(), subscription.isActive(),
                    Timestamp.valueOf(LocalDateTime.now()), subscription.getId());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteSubscription(String id) throws Exception {
        String sql = "DELETE FROM subscriptions WHERE id = ?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
