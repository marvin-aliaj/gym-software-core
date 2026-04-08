package com.spring.jdbc.gym.management.dao;

import com.spring.jdbc.gym.management.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ProductDao {
    private final JdbcTemplate jdbcTemplate;

    public ProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> getProductList() throws Exception {
        String sql = "SELECT * FROM products";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Product product = new Product();
                product.setId(rs.getString("id"));
                product.setTitle(rs.getString("title"));
                product.setDescription(rs.getString("description"));
                product.setStock(rs.getInt("stock"));
                product.setPrice(rs.getLong("price"));
                product.setCategoryId(rs.getString("category_id"));
                product.setBusinessId(rs.getString("business_id"));
                product.setCDate(rs.getString("cDate"));
                product.setMDate(rs.getString("mDate"));
                return product;
            });
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Product getProductById(String id) throws Exception {
        String sql = "SELECT * FROM products WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Product product = new Product();
                product.setId(rs.getString("id"));
                product.setTitle(rs.getString("title"));
                product.setDescription(rs.getString("description"));
                product.setStock(rs.getInt("stock"));
                product.setPrice(rs.getLong("price"));
                product.setCategoryId(rs.getString("category_id"));
                product.setBusinessId(rs.getString("business_id"));
                product.setCDate(rs.getString("cDate"));
                product.setMDate(rs.getString("mDate"));
                return product;
            }, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Product createProduct(Product product) throws Exception {
        String sql = "INSERT INTO products(title, description, stock, price, category_id, business_id, mDate, cDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, product.getTitle());
                ps.setString(2, product.getDescription());
                ps.setInt(3, product.getStock());
                ps.setLong(4, product.getPrice());
                ps.setString(5, product.getCategoryId());
                ps.setString(6, product.getBusinessId());
                ps.setObject(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setObject(8, Timestamp.valueOf(LocalDateTime.now()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                product.setId(keyHolder.getKeys().get("id").toString());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return product;
    }

    public void updateProduct(Product product) throws Exception {
        String sql = "UPDATE products SET title = ?, description = ?, stock = ?, price = ?, category_id = ?, business_id = ?, mDate = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql, product.getTitle(), product.getDescription(), product.getStock(),
                    product.getPrice(), product.getCategoryId(), product.getBusinessId(),
                    Timestamp.valueOf(LocalDateTime.now()), product.getId());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteProduct(String id) throws Exception {
        String sql = "DELETE FROM products WHERE id = ?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
