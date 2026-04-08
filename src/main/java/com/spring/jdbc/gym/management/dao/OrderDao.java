package com.spring.jdbc.gym.management.dao;

import com.spring.jdbc.gym.management.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderDao {
    private final JdbcTemplate jdbcTemplate;

    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Order> getOrderList() throws Exception {
        String sql = "SELECT * FROM order";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Order order = new Order();
                order.setId(rs.getString("id"));
                order.setBusinessId(rs.getString("business_id"));
                order.setUserId(rs.getString("user_id"));
                order.setShoppingCartId(rs.getString("shopping_cart_id"));
                order.setTotalAmount(rs.getLong("total_amount"));
                order.setAddress(rs.getString("address"));
                order.setCDate(rs.getString("cDate"));
                order.setMDate(rs.getString("mDate"));
                return order;
            });
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Order getOrderById(String id) throws Exception {
        String sql = "SELECT * FROM order WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Order order = new Order();
                order.setId(rs.getString("id"));
                order.setBusinessId(rs.getString("business_id"));
                order.setUserId(rs.getString("user_id"));
                order.setShoppingCartId(rs.getString("shopping_cart_id"));
                order.setTotalAmount(rs.getLong("total_amount"));
                order.setAddress(rs.getString("address"));
                order.setCDate(rs.getString("cDate"));
                order.setMDate(rs.getString("mDate"));
                return order;
            }, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Order createOrder(Order order) throws Exception {
        String sql = "INSERT INTO order(business_id, user_id, shopping_cart_id, total_amount, address, mDate, cDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, order.getBusinessId());
                ps.setString(2, order.getUserId());
                ps.setString(3, order.getShoppingCartId());
                ps.setLong(4, order.getTotalAmount());
                ps.setString(5, order.getAddress());
                ps.setObject(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setObject(7, Timestamp.valueOf(LocalDateTime.now()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                order.setId(keyHolder.getKeys().get("id").toString());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return order;
    }

    public void updateOrder(Order order) throws Exception {
        String sql = "UPDATE order SET business_id = ?, user_id = ?, shopping_cart_id = ?, total_amount = ?, address = ?, mDate = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql, order.getBusinessId(), order.getUserId(), order.getShoppingCartId(),
                    order.getTotalAmount(), order.getAddress(), Timestamp.valueOf(LocalDateTime.now()), order.getId());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteOrder(String id) throws Exception {
        String sql = "DELETE FROM order WHERE id = ?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
