package com.example.demo.service;
import com.example.demo.entity.Manager;
import com.example.demo.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    /**
     * 管理员登录逻辑
     * @return 登录成功返回 Manager 对象，失败返回 null
     */
    public Manager login(String username, String password) {
        Manager manager = managerRepository.findByUsername(username);

        if (manager != null) {
            // 这里为了简单演示直接比对明文密码
            // 实际项目中建议使用 BCryptPasswordEncoder 进行加密比对
            if (manager.getPassword().equals(password)) {
                return manager;
            }
        }
        return null;
    }
}