package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 注册用户
     */
    public boolean registerUser(User user) {
        // 1. 基本校验
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return false;
        }

        // 2. 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            return false;
        }

        // 3. 关于地址 (Address)
        // 以前是校验 Email 唯一性，现在改成了 Address。
        // 收货地址不需要唯一（室友/家人可能共用地址），所以这里不需要 existsByAddress 的检查
        // 我们只需要确保地址不为空即可（可选）
        if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
            // 如果你想强制要求填地址，就返回 false；如果不强制，这行可以删掉
            return false;
        }

        // 4. 保存用户
        userRepository.save(user);
        return true;
    }

    /**
     * 登录验证
     */
    public boolean loginUser(String username, String password) {
        if (username == null || password == null) return false;
        User user = userRepository.findByUsername(username);
        // 简单明文比对
        return user != null && user.getPassword().equals(password);
    }
    // 在 UserService.java 中
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}