package com.example.nanny.repository;

import com.example.nanny.domain.UserCamera;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserCameraRepository extends BaseMapper<UserCamera> {

    @Select("SELECT * FROM user_camera WHERE user_id = #{userId}")
    List<UserCamera> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_camera WHERE camera_id = #{cameraId}")
    List<UserCamera> findByCameraId(@Param("cameraId") String cameraId);

    @Select("SELECT COUNT(*) > 0 FROM user_camera WHERE user_id = #{userId} AND camera_id = #{cameraId}")
    boolean existsByUserIdAndCameraId(@Param("userId") Long userId, @Param("cameraId") String cameraId);
}
