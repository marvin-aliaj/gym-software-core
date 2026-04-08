package com.spring.jdbc.gym.management.dao;


import com.spring.jdbc.gym.management.model.Business;
import com.spring.jdbc.gym.management.model.enums.BUSINESS_TYPE;
import com.spring.jdbc.gym.management.model.filter.BusinessFilter;
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
public class BusinessDao {
    private final JdbcTemplate jdbcTemplate;

    public BusinessDao (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Business> getBusinessList (BusinessFilter filter) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("Select distinct businesses.* from businesses " +
                " left join user_businesses on businesses.id = user_businesses.business_id " +
                " where 1=1 ");
        if (filter.hasEntityId()) {
            sql.append(" and businesses.id = ?::uuid ");
            params.add(filter.getEntityId());
        }
        if (filter.hasUserIdSet()) {
            sql.append(" and user_businesses.user_id = ?::uuid ");
            params.add(filter.getUserId());
        }
        if (filter.hasSearchQuerySet()) {
            sql.append(" and (LOWER(name) LIKE ? OR LOWER(address) LIKE ?) ");
            String searchQuery = "%" + filter.getSearchQuery().toLowerCase() + "%";
            params.add(searchQuery);
            params.add(searchQuery);
        }
        sql.append(" Limit ? Offset ? ");
        params.add(filter.getLimit());
        params.add(filter.getOffset());

        String string_sql = sql.toString();
        try {
            return jdbcTemplate.query(string_sql, (rs, rowNum) -> {
                Business business = new Business ();
                business.setId(rs.getString("id"));
                business.setName(rs.getString("name"));
                business.setType(BUSINESS_TYPE.fromDescription(rs.getString("type")));
                business.setAddress(rs.getString("address"));
                business.setLatitude(rs.getBigDecimal("latitude"));
                business.setLongitude(rs.getBigDecimal("longitude"));
                business.setActive(rs.getBoolean("is_active"));
                business.setCDate(rs.getString("cDate"));
                business.setMDate(rs.getString("mDate"));
                return business;
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Business createGym(Business business) throws Exception {
        String sql = "Insert into businesses(name, type, address, latitude, longitude, is_active, mDate, cDate) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

                ps.setString(1, business.getName());
                ps.setString(2, business.getType().getDescription());
                ps.setString(3, business.getAddress());
                ps.setBigDecimal(4, business.getLatitude());
                ps.setBigDecimal(5, business.getLongitude());
                ps.setBoolean(6, business.isActive());
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                return ps;
            }, keyHolder);

            // Retrieve UUID safely
            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                business.setId(keyHolder.getKeys().get("id").toString());
            }

        } catch (Exception e) {
            throw new Exception(e);
        }
        return business;
    }

    public void updateGym(Business business) throws Exception {
        String sql = "UPDATE businesses SET name = ?, type = ?, address = ?, latitude = ?, " +
                "longitude = ?, is_active = ?, mDate = ? WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql,
                    business.getName(),
                    business.getType().getDescription(),
                    business.getAddress(),
                    business.getLatitude(),
                    business.getLongitude(),
                    business.isActive(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    business.getId());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteGym(String businessId) throws Exception {
        String sql = "DELETE FROM businesses WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql, businessId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
