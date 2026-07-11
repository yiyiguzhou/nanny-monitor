package com.example.nanny.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nanny.domain.AlertRecord;
import com.example.nanny.service.AlertRecordService;
import com.example.nanny.mapper.AlertRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
* @author wzhang
* @description 针对表【alert_record】的数据库操作Service实现
* @createDate 2026-06-20 22:54:55
*/
@Service
@DubboService(interfaceClass = AlertRecordService.class, version = "1.0.0", group = "HSF")
public class AlertRecordServiceImpl extends ServiceImpl<AlertRecordMapper, AlertRecord>
    implements AlertRecordService{

}




