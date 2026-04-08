package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.ProductDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> getProductList() throws Exception {
        try {
            return productDao.getProductList();
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public Product getProductById(String id) throws Exception {
        try {
            return productDao.getProductById(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public Product createProduct(Product product) throws Exception {
        try {
            return productDao.createProduct(product);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void updateProduct(Product product) throws Exception {
        try {
            productDao.updateProduct(product);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void deleteProduct(String id) throws Exception {
        try {
            productDao.deleteProduct(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }
}
