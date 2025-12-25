package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 1. 检查用户名是否存在
     * 对应报错：existsByUsername
     */
    public boolean existsByUsername(String username) {
        // SQL: 统计叫这个名字的人有几个
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";

        //如果您数据库表名叫 users，请改为: "SELECT COUNT(*) FROM users WHERE username = ?"

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * 2. 保存/注册新用户
     * 对应报错：save
     */
    public boolean save(User user) {
        // SQL: 插入一条新数据
        // 注意：这里假设您的数据库字段是 username, password, address, phone
        // 如果没有 phone 字段，请把 sql 里的 phone 删掉
        String sql = "INSERT INTO user (id,username, password, address) VALUES (?, ?, ?, ?)";

        int rowsAffected = jdbcTemplate.update(sql,
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getAddress()
        );

        return rowsAffected > 0;
    }

    /**
     * 3. 根据用户名查找用户详情 (用于登录和结算)
     * 之前提到的方法
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(User.class),
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}