package com.example.nanny.repository;

import com.example.nanny.domain.PaymentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PaymentRecordRepository extends BaseMapper<PaymentRecord> {

    @Select("SELECT * FROM payment_record WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<PaymentRecord> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM payment_record WHERE transaction_id = #{transactionId}")
    PaymentRecord findByTransactionId(@Param("transactionId") String transactionId);
}
