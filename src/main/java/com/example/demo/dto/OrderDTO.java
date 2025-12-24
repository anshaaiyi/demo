package com.example.demo.dto;
import java.util.List;

public class OrderDTO {
    private Double totalAmount;

    // ✅ 【关键修改】改回 username，为了匹配前端传过来的 JSON key
    // 前端传的是 { "username": "..." }，这里必须叫 username 才能接住
    private String username;
    private String address;
    private List<OrderItemDTO> items;

    // Getters & Setters
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    // ✅ Getter/Setter 也要改名
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    public static class OrderItemDTO {
        private Long menuItemId;
        private Integer quantity;
        private Double price;

        public Long getMenuItemId() { return menuItemId; }
        public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }
}