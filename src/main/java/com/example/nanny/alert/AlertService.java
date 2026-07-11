package com.example.nanny.alert;

import com.example.nanny.domain.AlertRecord;
import com.example.nanny.repository.AlertRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRecordRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public AlertService(AlertRecordRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    public AlertRecord raise(String cameraId, String type, String reason) {
        AlertRecord record = new AlertRecord(cameraId, type, reason);
        repository.insert(record);
        messagingTemplate.convertAndSend("/topic/alerts", record);
        messagingTemplate.convertAndSend("/topic/cameras/" + cameraId + "/alerts", record);
        log.warn("Alert raised. cameraId={}, type={}, reason={}", cameraId, type, reason);
        return record;
    }
}