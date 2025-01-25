-- 清空现有数据
TRUNCATE TABLE phone_numbers;

-- 插入20个手机号（其中10个带微信号）
INSERT INTO phone_numbers 
(phone_number, type, wechat_number, status, last_used_time, version) 
VALUES
-- 带微信号的手机号
('13800000001', 'PHONE', 'wx_user001', 1, NULL, 0),
('13800000002', 'PHONE', 'wx_user002', 1, NULL, 0),
('13800000003', 'PHONE', 'wx_user003', 1, NULL, 0),
('13800000004', 'PHONE', 'wx_user004', 1, NULL, 0),
('13800000005', 'PHONE', 'wx_user005', 1, NULL, 0),
('13800000006', 'PHONE', 'wx_user006', 1, NULL, 0),
('13800000007', 'PHONE', 'wx_user007', 1, NULL, 0),
('13800000008', 'PHONE', 'wx_user008', 1, NULL, 0),
('13800000009', 'PHONE', 'wx_user009', 1, NULL, 0),
('13800000010', 'PHONE', 'wx_user010', 1, NULL, 0),

-- 不带微信号的手机号
('13900000001', 'PHONE', NULL, 1, NULL, 0),
('13900000002', 'PHONE', NULL, 1, NULL, 0),
('13900000003', 'PHONE', NULL, 1, NULL, 0),
('13900000004', 'PHONE', NULL, 1, NULL, 0),
('13900000005', 'PHONE', NULL, 1, NULL, 0),
('13900000006', 'PHONE', NULL, 0, NULL, 0), -- 设置为禁用状态
('13900000007', 'PHONE', NULL, 0, NULL, 0), -- 设置为禁用状态
('13900000008', 'PHONE', NULL, 1, NULL, 0),
('13900000009', 'PHONE', NULL, 1, NULL, 0),
('13900000010', 'PHONE', NULL, 1, NULL, 0),

-- 插入10个纯微信号
('wx_account001', 'WECHAT', NULL, 1, NULL, 0),
('wx_account002', 'WECHAT', NULL, 1, NULL, 0),
('wx_account003', 'WECHAT', NULL, 1, NULL, 0),
('wx_account004', 'WECHAT', NULL, 1, NULL, 0),
('wx_account005', 'WECHAT', NULL, 0, NULL, 0), -- 设置为禁用状态
('wx_account006', 'WECHAT', NULL, 1, NULL, 0),
('wx_account007', 'WECHAT', NULL, 1, NULL, 0),
('wx_account008', 'WECHAT', NULL, 1, NULL, 0),
('wx_account009', 'WECHAT', NULL, 1, NULL, 0),
('wx_account010', 'WECHAT', NULL, 1, NULL, 0); 