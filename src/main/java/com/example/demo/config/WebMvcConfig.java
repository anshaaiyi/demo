package com.example.demo.config; // <--- 注意这里必须对应你的文件夹路径

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 务必确保这个文件夹在你的 D 盘是真实存在的！！
    public static final String UPLOAD_FOLDER = "D:/my_order_project/images/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + UPLOAD_FOLDER);
    }
}