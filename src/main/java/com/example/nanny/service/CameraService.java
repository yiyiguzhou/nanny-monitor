package com.example.nanny.service;

import com.example.nanny.domain.Camera;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wzhang
* @description 针对表【camera】的数据库操作Service
* @createDate 2026-06-20 22:54:55
*/
public interface CameraService extends IService<Camera> {

    Camera register(String id, String name, String rtspUrl, Long userId);

    List<Camera> listAll(Long userId, String role);

    void start(String id, Long userId);

    void stop(String id, Long userId);
}
