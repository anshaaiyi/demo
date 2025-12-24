package com.example.demo.controller;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.Order; // 1. 导入实体
import com.example.demo.repository.OrderRepository; // 1. 导入 Repository
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin // 允许 Vue 访问
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 2. 注入 Repository 用于查询列表
    @Autowired
    private OrderRepository orderRepository;

    // 前端调用: axios.post('/api/order/create', orderData)
    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestBody OrderDTO orderDTO) {
        Long orderId = orderService.createOrder(orderDTO);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "下单成功");
        result.put("orderId", orderId);
        return result;
    }
    @DeleteMapping("/delete/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
    }
    // --- 【新增】管理员获取所有订单接口 ---
    // URL: http://localhost:8080/api/order/list

    @GetMapping("/list")
    public Iterable<Order> getAllOrders() {
        // Spring Data JDBC 的 findAll 会自动查询 orders 表
        // 并且自动把关联的 order_items 查询出来填入 items 集合中
        return orderRepository.findAll();
    }
}