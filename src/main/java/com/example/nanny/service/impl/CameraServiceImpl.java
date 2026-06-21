package com.example.nanny.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nanny.domain.Camera;
import com.example.nanny.service.CameraService;
import com.example.nanny.mapper.CameraMapper;
import org.springframework.stereotype.Service;

/**
* @author wzhang
* @description 针对表【camera】的数据库操作Service实现
* @createDate 2026-06-20 22:54:55
*/
@Service
public class CameraServiceImpl extends ServiceImpl<CameraMapper, Camera>
    implements CameraService{

}




