package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 检查用户名是否可用
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        String msg = exists ? "用户名已存在" : "用户名可用";
        return ResponseEntity.ok(new ResponseMessage(msg, null));
    }

    // 2. 注册接口
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("用户名或密码不能为空", null));
        }

        boolean success = userService.registerUser(user);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseMessage("注册成功!", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("用户名或邮箱已存在!", null));
        }
    }

    // 3. 登录接口（已修改：返回用户信息）
    // 在 UserController.java 中
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        // 1. 验证账号密码
        boolean isValid = userService.loginUser(user.getUsername(), user.getPassword());

        if (isValid) {
            // 2. 查出用户详细信息 (这一步至关重要！)
            User loggedInUser = userService.findByUsername(user.getUsername());

            // 3. 把信息打包 (除了密码，其他的都发给前端)
            Map<String, Object> userData = new HashMap<>();
            if (loggedInUser != null) {
                userData.put("id", loggedInUser.getId());
                userData.put("username", loggedInUser.getUsername());
                userData.put("address", loggedInUser.getAddress());
            }

            // 4. ✅ 重点：这里必须把 userData 放进去！
            // 如果你的 ResponseMessage 只有 message 一个参数，请看下面的 "补充修正"
            return ResponseEntity.ok()
                    .body(new ResponseMessage("登录成功!", userData));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseMessage("用户名或密码错误!", null));
        }
    }

    // 内部类：统一响应格式
    // UserController.java 内部类
    public static class ResponseMessage {
        private String message;
        private Object data; // ✅ 必须有这个字段用来装用户信息

        public ResponseMessage(String message, Object data) {
            this.message = message;
            this.data = data;
        }

        public String getMessage() { return message; }
        public Object getData() { return data; }
    }
}