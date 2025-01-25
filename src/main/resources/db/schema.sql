CREATE TABLE IF NOT EXISTS phone_numbers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    type VARCHAR(10) NOT NULL DEFAULT 'PHONE' COMMENT 'PHONE:手机号,WECHAT:微信号',
    wechat_number VARCHAR(20) COMMENT '关联的微信号',
    status TINYINT(1) DEFAULT 1 COMMENT '1:active, 0:inactive',
    last_used_time DATETIME,
    version BIGINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status_last_used (status, last_used_time),
    INDEX idx_phone_status (phone_number, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 