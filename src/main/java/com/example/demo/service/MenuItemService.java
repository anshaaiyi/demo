package com.example.demo.service;

import com.example.demo.config.WebMvcConfig;
import com.example.demo.entity.MenuItem;
import com.example.demo.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    /**
     * 添加菜品逻辑：上传图片 -> 保存文件 -> 保存数据到DB
     */
    public MenuItem addMenuItem(String name, Double price, String description, MultipartFile file) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);

        // 如果上传了文件
        if (file != null && !file.isEmpty()) {
            // 1. 获取文件后缀 (如 .png, .jpg)
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 2. 生成随机文件名，防止重名 (如 123e4567-e89b... .jpg)
            String fileName = UUID.randomUUID().toString() + suffix;

            // 3. 准备目标文件对象
            File destFile = new File(WebMvcConfig.UPLOAD_FOLDER + fileName);

            // 4. 自动创建父目录 (如果 D:/my_order_project/images/ 不存在)
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }

            try {
                // 5. 写入硬盘
                file.transferTo(destFile);

                // 6. 将 Web 访问路径存入实体对象
                // 注意：这里存的是相对路径，如 /images/xxx.jpg
                item.setImageUrl("/images/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("图片保存失败: " + e.getMessage());
            }
        } else {
            // 没传图就设为 null，前端会显示默认图
            item.setImageUrl(null);
        }

        // 7. 存入数据库 (Spring Data JDBC 会自动处理 Insert)
        return menuItemRepository.save(item);
    }
}