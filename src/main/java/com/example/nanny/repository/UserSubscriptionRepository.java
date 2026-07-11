package com.example.nanny.repository;

import com.example.nanny.domain.UserSubscription;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserSubscriptionRepository extends BaseMapper<UserSubscription> {

    @Select("SELECT * FROM user_subscription WHERE user_id = #{userId} AND status = 'ACTIVE' AND end_date > NOW() ORDER BY end_date DESC LIMIT 1")
    UserSubscription findActiveByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_subscription WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserSubscription> findByUserId(@Param("userId") Long userId);
}
