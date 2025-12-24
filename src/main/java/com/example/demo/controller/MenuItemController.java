package com.example.demo.controller;

import com.example.demo.entity.MenuItem;
import com.example.demo.repository.MenuItemRepository; // 1. 导入 Repository
import com.example.demo.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin // 允许 Vue 跨域
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    // 2. 注入 Repository 用于简单的删除操作
    @Autowired
    private MenuItemRepository menuItemRepository;

    // 前端获取列表: axios.get('/api/menu/list')
    @GetMapping("/list")
    public List<MenuItem> getList() {
        return menuItemService.findAll();
    }

    // 管理员添加菜品
    // URL: http://localhost:8080/api/menu/add
    @PostMapping("/add")
    public MenuItem add(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        return menuItemService.addMenuItem(name, price, description, image);
    }

    // --- 【新增】删除菜品接口 ---
    // URL: http://localhost:8080/api/menu/delete/{id}
    @DeleteMapping("/delete/{id}")
    public void deleteMenu(@PathVariable Long id) {
        // 直接调用 Repository 删除数据库记录
        menuItemRepository.deleteById(id);
    }
}