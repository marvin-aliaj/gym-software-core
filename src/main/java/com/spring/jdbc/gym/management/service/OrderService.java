package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.OrderDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public List<Order> getOrderList() throws Exception {
        try {
            return orderDao.getOrderList();
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public Order getOrderById(String id) throws Exception {
        try {
            return orderDao.getOrderById(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public Order createOrder(Order order) throws Exception {
        try {
            return orderDao.createOrder(order);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void updateOrder(Order order) throws Exception {
        try {
            orderDao.updateOrder(order);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void deleteOrder(String id) throws Exception {
        try {
            orderDao.deleteOrder(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }
}
