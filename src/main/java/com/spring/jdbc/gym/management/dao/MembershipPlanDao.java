package com.spring.jdbc.gym.management.dao;

import com.spring.jdbc.gym.management.model.MembershipPlan;
import com.spring.jdbc.gym.management.model.filter.MembershipPlanFilter;
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
public class MembershipPlanDao {
    private final JdbcTemplate jdbcTemplate;

    public MembershipPlanDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MembershipPlan> getMembershipPlanList(MembershipPlanFilter filter) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT mp.* FROM membership_plans mp "
        );
        
        // JOIN with subscriptions if filtering by userId
        if (filter.hasUserIdSet()) {
            sql.append("INNER JOIN subscriptions s ON mp.id = s.membership_plan_id ");
        }
        
        sql.append("WHERE 1=1 ");
        
        if (filter.hasEntityId()) {
            sql.append("AND mp.id = ?::uuid ");
            params.add(filter.getEntityId());
        }
        
        if (filter.hasBusinessIdSet()) {
            sql.append("AND mp.gym_id = ?::uuid ");
            params.add(filter.getBusinessId());
        }
        
        if (filter.hasUserIdSet()) {
            sql.append("AND s.user_id = ?::uuid ");
            params.add(filter.getUserId());
        }
        
        if (filter.hasIsActiveSet()) {
            sql.append("AND mp.is_active = ? ");
            params.add(filter.getIsActive());
        }
        
        sql.append("LIMIT ? OFFSET ? ");
        params.add(filter.getLimit());
        params.add(filter.getOffset());
        
        try {
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
                MembershipPlan membershipPlan = new MembershipPlan();
                membershipPlan.setId(rs.getString("id"));
                membershipPlan.setGymId(rs.getString("gym_id"));
                membershipPlan.setPlanName(rs.getString("plan_name"));
                membershipPlan.setAmountCents(rs.getLong("price_cents"));
                membershipPlan.setDurationDays(rs.getInt("duration_days"));
                membershipPlan.setDescription(rs.getString("description"));
                membershipPlan.setActive(rs.getBoolean("is_active"));
                membershipPlan.setCDate(rs.getString("cDate"));
                membershipPlan.setMDate(rs.getString("mDate"));
                return membershipPlan;
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public MembershipPlan getMembershipPlanById(String id) throws Exception {
        String sql = "SELECT * FROM membership_plans WHERE id = ?::uuid";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                MembershipPlan membershipPlan = new MembershipPlan();
                membershipPlan.setId(rs.getString("id"));
                membershipPlan.setGymId(rs.getString("gym_id"));
                membershipPlan.setPlanName(rs.getString("plan_name"));
                membershipPlan.setAmountCents(rs.getLong("price_cents"));
                membershipPlan.setDurationDays(rs.getInt("duration_days"));
                membershipPlan.setDescription(rs.getString("description"));
                membershipPlan.setActive(rs.getBoolean("is_active"));
                membershipPlan.setCDate(rs.getString("cDate"));
                membershipPlan.setMDate(rs.getString("mDate"));
                return membershipPlan;
            }, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public MembershipPlan createMembershipPlan(MembershipPlan membershipPlan) throws Exception {
        String sql = "INSERT INTO membership_plans(gym_id, plan_name, price_cents, duration_days, description, is_active, mDate, cDate) " +
                "VALUES (?::uuid, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setObject(1, membershipPlan.getGymId());
                ps.setString(2, membershipPlan.getPlanName());
                ps.setLong(3, membershipPlan.getAmountCents());
                ps.setInt(4, membershipPlan.getDurationDays());
                ps.setString(5, membershipPlan.getDescription());
                ps.setBoolean(6, membershipPlan.isActive());
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                membershipPlan.setId(keyHolder.getKeys().get("id").toString());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return membershipPlan;
    }

    public void updateMembershipPlan(MembershipPlan membershipPlan) throws Exception {
        String sql = "UPDATE membership_plans SET gym_id = ?::uuid, plan_name = ?, price_cents = ?, duration_days = ?, description = ?, is_active = ?, mDate = ? WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql, membershipPlan.getGymId(), membershipPlan.getPlanName(),
                    membershipPlan.getAmountCents(), membershipPlan.getDurationDays(),
                    membershipPlan.getDescription(), membershipPlan.isActive(),
                    Timestamp.valueOf(LocalDateTime.now()), membershipPlan.getId());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteMembershipPlan(String id) throws Exception {
        String sql = "DELETE FROM membership_plans WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
