START TRANSACTION;

DELETE FROM phone_numbers;

INSERT INTO phone_numbers (phone_number, type, status, version, last_used_time) VALUES
('13800000001', 'PHONE', true, 0, NULL),
('13800000002', 'PHONE', true, 0, NULL),
('13800000003', 'PHONE', true, 0, NULL),
('13800000004', 'PHONE', true, 0, NULL),
('13800000005', 'PHONE', true, 0, NULL);

COMMIT;