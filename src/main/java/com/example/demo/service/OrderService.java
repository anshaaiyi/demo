package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        // 1. 创建订单主对象
        Order order = new Order();
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setStatus("PAID");
        order.setCreateTime(LocalDateTime.now());

        // 映射用户信息
        order.setCustomerName(orderDTO.getUsername()); // 注意这里用的是 DTO.getUsername()
        order.setAddress(orderDTO.getAddress());

        // 2. 转换子项
        if (orderDTO.getItems() != null) {
            for (OrderDTO.OrderItemDTO itemDTO : orderDTO.getItems()) {
                OrderItem item = new OrderItem(
                        itemDTO.getMenuItemId(),
                        itemDTO.getQuantity(),
                        itemDTO.getPrice()
                );
                order.addItem(item);
            }
        }

        // 3. 保存并返回 ID
        // ✅ 修正点：先拿到对象，再返回 ID
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }
}