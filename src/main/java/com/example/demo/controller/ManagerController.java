package com.example.demo.controller;

import com.example.demo.entity.Manager;
import com.example.demo.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/manage")
@CrossOrigin // 允许跨域
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    // 登录接口
    // POST http://localhost:8080/api/manage/login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = new HashMap<>();

        Manager manager = managerService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (manager != null) {
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("username", manager.getUsername());
            // 你可以在这里返回 token，或者仅仅返回成功状态
        } else {
            result.put("success", false);
            result.put("message", "账号或密码错误");
        }
        return result;
    }

    // 接收前端 JSON 的简单的 DTO 类
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}