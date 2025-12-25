package com.example.demo.repository;
import com.example.demo.entity.Manager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends CrudRepository<Manager, Long> {
    // 增加一个根据用户名查找的方法，用于登录验证
    Manager findByUsername(String username);
}