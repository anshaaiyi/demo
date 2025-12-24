package com.example.demo.repository;
import com.example.demo.entity.MenuItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends CrudRepository<MenuItem, Long> {
    // 覆盖 findAll 返回 List 而不是 Iterable，方便 Controller 使用
    @Override
    List<MenuItem> findAll();
}