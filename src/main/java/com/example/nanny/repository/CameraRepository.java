package com.example.nanny.repository;

import com.example.nanny.domain.Camera;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CameraRepository extends BaseMapper<Camera> {

    /**
     * 查询前10条摄像头数据
     */
    @Select("SELECT * FROM camera LIMIT 10")
    List<Camera> findTop10By();
}