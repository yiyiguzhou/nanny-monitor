-- nanny-monitor database schema
-- Auto-executed by Spring Boot sql.init.mode=always

CREATE TABLE IF NOT EXISTS `user` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `username`   VARCHAR(64)  NOT NULL,
    `password`   VARCHAR(255) NOT NULL,
    `role`       VARCHAR(16)  NOT NULL DEFAULT 'USER',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user_camera` (
    `user_id`   BIGINT      NOT NULL,
    `camera_id` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`user_id`, `camera_id`),
    INDEX `idx_camera_id` (`camera_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `camera` (
    `id`       VARCHAR(64)  NOT NULL,
    `name`     VARCHAR(128) NOT NULL,
    `rtsp_url` VARCHAR(512) NOT NULL,
    `active`   INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `alert_record` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `camera_id`  VARCHAR(64)  NOT NULL,
    `alert_type` VARCHAR(64)  NOT NULL,
    `reason`     VARCHAR(512) NOT NULL,
    `alerted_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_camera_id` (`camera_id`),
    INDEX `idx_alerted_at` (`alerted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `detection_record` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT,
    `camera_id`          VARCHAR(64)  NOT NULL,
    `feeding`            INT          NOT NULL DEFAULT 0,
    `baby_present`       INT          NOT NULL DEFAULT 0,
    `caregiver_present`  INT          NOT NULL DEFAULT 0,
    `abnormal`           INT          NOT NULL DEFAULT 0,
    `confidence`         DOUBLE       NOT NULL DEFAULT 0,
    `description`        VARCHAR(512) NOT NULL DEFAULT '',
    `detected_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_camera_id` (`camera_id`),
    INDEX `idx_detected_at` (`detected_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `video_clip` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `camera_id`        VARCHAR(64)  NOT NULL,
    `alert_record_id`  BIGINT       DEFAULT NULL,
    `alert_type`       VARCHAR(64)  NOT NULL,
    `clip_data`        LONGBLOB     NOT NULL,
    `duration_seconds` DOUBLE       DEFAULT NULL,
    `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_camera_id` (`camera_id`),
    INDEX `idx_alert_record_id` (`alert_record_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user_subscription` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT      NOT NULL,
    `plan`       VARCHAR(16) NOT NULL COMMENT 'MONTHLY/QUARTERLY/YEARLY',
    `start_date` DATETIME    NOT NULL,
    `end_date`   DATETIME    NOT NULL,
    `status`     VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/EXPIRED/CANCELLED',
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status_end_date` (`status`, `end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `payment_record` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`          BIGINT       NOT NULL,
    `amount`           DECIMAL(10,2) NOT NULL,
    `method`           VARCHAR(16)  NOT NULL COMMENT 'WECHAT/ALIPAY/BANK_CARD',
    `subscription_plan` VARCHAR(16) NOT NULL COMMENT 'MONTHLY/QUARTERLY/YEARLY',
    `status`           VARCHAR(16)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/SUCCESS/FAILED/REFUNDED',
    `transaction_id`   VARCHAR(128) DEFAULT NULL,
    `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
