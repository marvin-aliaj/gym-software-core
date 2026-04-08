package com.spring.jdbc.gym.management.dao;


import com.spring.jdbc.gym.management.model.User;
import com.spring.jdbc.gym.management.model.enums.BUSINESS_TYPE;
import com.spring.jdbc.gym.management.model.enums.USER_ROLE;
import com.spring.jdbc.gym.management.model.enums.USER_STATUS;
import com.spring.jdbc.gym.management.model.filter.UserFilter;
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
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getUserList(UserFilter filter) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT users.* " +
                        ", b.id as business_id" +
                        ", b.name as business_name" +
                        ", b.address as business_address" +
                        ", b.latitude as business_latitude" +
                        ", b.longitude as business_longitude" +
                        ", b.is_active as business_is_active" +
                        ", b.type as business_type" +
                        ", b.cDate as business_cDate" +
                        ", b.mDate as business_mDate " +
                "FROM users " +
                "LEFT JOIN user_businesses ub ON users.id = ub.user_id " +
                "LEFT JOIN businesses b ON ub.business_id = b.id " +
                "WHERE 1=1 ");
        
        if (filter.hasEntityId()) {
            sql.append(" AND users.id = ?::uuid ");
            params.add(filter.getEntityId());
        }
        if (filter.hasRoleSet()) {
            sql.append(" AND users.role = ? ");
            params.add(USER_ROLE.fromCode(filter.getRole()).getDescription());
        }
        if (filter.hasGymIdSet()) {
            sql.append(" AND ub.business_id = ?::uuid ");
            params.add(filter.getGymId());
        }
        if (filter.hasEmailSet() && filter.hasPasswordSet()) {
            sql.append(" AND LOWER(users.email) = ? AND users.password_hash = ?");
            params.add(filter.getEmail().toLowerCase());
            params.add(filter.getPassword());
        }
        if (filter.hasSearchQuerySet()) {
            sql.append(" AND (LOWER(users.first_name) LIKE ? OR LOWER(users.last_name) LIKE ?)");
            String searchQuery = "%" + filter.getSearchQuery().toLowerCase() + "%";
            params.add(searchQuery);
            params.add(searchQuery);
        }
        
        sql.append(" ORDER BY users.id ");
        sql.append(" LIMIT ? OFFSET ? ");
        params.add(filter.getLimit());
        params.add(filter.getOffset());

        String string_sql = sql.toString();
        try {
            return jdbcTemplate.query(string_sql, rs -> {
                java.util.Map<String, User> userMap = new java.util.LinkedHashMap<>();
                
                while (rs.next()) {
                    String userId = rs.getString("id");

                    User user = userMap.get(userId);
                    if (user == null) {
                        user = new User();
                        user.setId(userId);
                        user.setFirstName(rs.getString("first_name"));
                        user.setLastName(rs.getString("last_name"));
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone"));
                        user.setRole(USER_ROLE.fromDescription(rs.getString("role")));
                        user.setStatus(USER_STATUS.fromDescription(rs.getString("status")));
                        user.setCDate(rs.getString("cDate"));
                        user.setBusinesses(new ArrayList<>());
                        userMap.put(userId, user);
                    }

                    String businessId = rs.getString("business_id");
                    if (businessId != null) {
                        com.spring.jdbc.gym.management.model.Business business = 
                            new com.spring.jdbc.gym.management.model.Business();
                        business.setId(businessId);
                        business.setName(rs.getString("business_name"));
                        business.setType(BUSINESS_TYPE.fromDescription(rs.getString("business_type")));
                        business.setAddress(rs.getString("business_address"));
                        business.setLatitude(rs.getBigDecimal("business_latitude"));
                        business.setLongitude(rs.getBigDecimal("business_longitude"));
                        business.setActive(rs.getBoolean("business_is_active"));
                        business.setCDate(rs.getString("business_cDate"));
                        business.setMDate(rs.getString("business_mDate"));
                        user.getBusinesses().add(business);
                    }
                }
                
                return new ArrayList<>(userMap.values());
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public User createUser(User user) throws Exception {
        String sql = "Insert into users(username, first_name, last_name, email, password_hash, role, status, phone, gender, mDate, cDate) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

                ps.setString(1, user.getUsername());
                ps.setString(2, user.getFirstName());
                ps.setString(3, user.getLastName());
                ps.setString(4, user.getEmail ().toLowerCase());
                ps.setString(5, user.getPassword());

                ps.setString(6, user.getRole().getDescription());
                ps.setString(7, user.getStatus().getDescription());

                ps.setString(8, user.getPhone());
                ps.setString(9, user.getGender());
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                return ps;
            }, keyHolder);

            // Retrieve UUID safely
            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                user.setId(keyHolder.getKeys().get("id").toString());
            }

        } catch (Exception e) {
            throw new Exception(e);
        }
        return user;
    }

    public void createUserBusinesses(User user) throws Exception {
        if (user.getBusinesses() == null || user.getBusinesses().isEmpty()) {
            return;
        }

        String sql = "Insert into user_businesses(user_id, business_id, mDate, cDate) " +
                " VALUES (?, ?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, user.getBusinesses(), user.getBusinesses().size(),
                (PreparedStatement ps, com.spring.jdbc.gym.management.model.Business business) -> {
                    ps.setObject(1, java.util.UUID.fromString(user.getId()));
                    ps.setObject(2, java.util.UUID.fromString(business.getId()));
                    ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                });
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void updateUser(User user) throws Exception {
        String sql = "UPDATE users SET username = ?, first_name = ?, last_name = ?, email = ?, " +
                "phone = ?, gender = ?, role = ?, status = ?, mDate = ? WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql,
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail().toLowerCase(),
                    user.getPhone(),
                    user.getGender(),
                    user.getRole().getDescription(),
                    user.getStatus().getDescription(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    user.getId());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteUser(String userId) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?::uuid";
        try {
            jdbcTemplate.update(sql, userId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
