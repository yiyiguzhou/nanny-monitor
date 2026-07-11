package com.example.nanny.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nanny.domain.Camera;
import com.example.nanny.domain.UserCamera;
import com.example.nanny.service.CameraService;
import com.example.nanny.mapper.CameraMapper;
import com.example.nanny.repository.UserCameraRepository;
import com.example.nanny.stream.StreamManager;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author wzhang
* @description 针对表【camera】的数据库操作Service实现
* @createDate 2026-06-20 22:54:55
*/
@Service
@DubboService(interfaceClass = CameraService.class, version = "1.0.0", group = "HSF")
public class CameraServiceImpl extends ServiceImpl<CameraMapper, Camera>
    implements CameraService {

    private final StreamManager streamManager;
    private final UserCameraRepository userCameraRepository;

    public CameraServiceImpl(StreamManager streamManager,
                             UserCameraRepository userCameraRepository) {
        this.streamManager = streamManager;
        this.userCameraRepository = userCameraRepository;
    }

    @Override
    public Camera register(String id, String name, String rtspUrl, Long userId) {
        Camera camera = new Camera();
        camera.setId(id);
        camera.setName(name);
        camera.setRtspUrl(rtspUrl);
        camera.setActive(0);
        save(camera);
        userCameraRepository.insert(new UserCamera(userId, id));
        return camera;
    }

    @Override
    public List<Camera> listAll(Long userId, String role) {
        if ("ADMIN".equals(role)) {
            return list();
        }
        List<UserCamera> mappings = userCameraRepository.findByUserId(userId);
        List<String> cameraIds = mappings.stream()
            .map(UserCamera::getCameraId)
            .collect(Collectors.toList());
        if (cameraIds.isEmpty()) {
            return List.of();
        }
        return listByIds(cameraIds);
    }

    @Override
    public void start(String id, Long userId) {
        checkOwnership(id, userId);
        Camera camera = getById(id);
        if (camera == null) {
            throw new IllegalArgumentException("Camera not found: " + id);
        }
        streamManager.start(id, camera.getRtspUrl());
        camera.setActive(1);
        updateById(camera);
    }

    @Override
    public void stop(String id, Long userId) {
        checkOwnership(id, userId);
        streamManager.stop(id);
        Camera camera = getById(id);
        if (camera != null) {
            camera.setActive(0);
            updateById(camera);
        }
    }

    private void checkOwnership(String cameraId, Long userId) {
        if (!userCameraRepository.existsByUserIdAndCameraId(userId, cameraId)) {
            throw new IllegalArgumentException("Camera not found or access denied: " + cameraId);
        }
    }
}
