package com.example.nanny.repository;

import com.example.nanny.domain.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CameraRepository extends JpaRepository<Camera, String> {

    /**
     * 查询前10条摄像头数据
     */
    /**
     * 查找前10条摄像头数据
     * 
     * 具体实现说明：
     * 在 Spring Data JPA 中，接口方法名即为具体实现。
     * 框架会在运行时自动解析方法名中的 "Top10" 关键字，
     * 动态生成并执行带有 "LIMIT 10"（或对应数据库方言限制语法）的 SQL 查询，
     * 无需手动编写 @Query 或实现类。
     */
    List<Camera> findTop10By();
}
