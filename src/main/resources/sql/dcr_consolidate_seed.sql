WITH seed_reports (
    employee_id,
    report_date,
    working_status,
    reported_from,
    day_start_time,
    day_end_time,
    first_call_time,
    last_call_time,
    submitted_at,
    is_delayed,
    is_deviated,
    joint_work_with,
    remark
) AS (
    VALUES
        (7, DATE '2026-01-05', 'Field Work', 'MOB', TIMESTAMP '2026-01-05 09:10:00', TIMESTAMP '2026-01-05 18:20:00', TIMESTAMP '2026-01-05 10:05:00', TIMESTAMP '2026-01-05 17:45:00', TIMESTAMP '2026-01-05 18:30:00', FALSE, FALSE, NULL, 'Regular field work day'),
        (7, DATE '2026-01-06', 'Field Work', 'MOB', TIMESTAMP '2026-01-06 09:05:00', TIMESTAMP '2026-01-06 18:05:00', TIMESTAMP '2026-01-06 10:15:00', TIMESTAMP '2026-01-06 16:55:00', TIMESTAMP '2026-01-06 18:15:00', FALSE, FALSE, 'Kunal Verma', 'Joint work day'),
        (7, DATE '2026-01-12', 'Meeting', 'API GRV', TIMESTAMP '2026-01-12 10:00:00', TIMESTAMP '2026-01-12 15:30:00', NULL, NULL, TIMESTAMP '2026-01-12 16:00:00', FALSE, FALSE, NULL, 'Review meeting'),
        (7, DATE '2026-01-20', 'Field Work', 'IOS', TIMESTAMP '2026-01-20 09:25:00', TIMESTAMP '2026-01-20 19:00:00', TIMESTAMP '2026-01-20 10:20:00', TIMESTAMP '2026-01-20 18:10:00', TIMESTAMP '2026-01-21 08:10:00', TRUE, FALSE, NULL, 'Submitted next morning'),
        (7, DATE '2026-02-03', 'Field Work', 'MOB', TIMESTAMP '2026-02-03 09:15:00', TIMESTAMP '2026-02-03 18:30:00', TIMESTAMP '2026-02-03 10:00:00', TIMESTAMP '2026-02-03 17:40:00', TIMESTAMP '2026-02-03 18:40:00', FALSE, TRUE, NULL, 'TP deviation marked'),
        (7, DATE '2026-02-10', 'Leave', 'API GRV', TIMESTAMP '2026-02-10 09:00:00', TIMESTAMP '2026-02-10 09:00:00', NULL, NULL, TIMESTAMP '2026-02-10 09:10:00', FALSE, FALSE, NULL, 'Approved leave'),
        (7, DATE '2026-03-02', 'Field Work', 'MOB', TIMESTAMP '2026-03-02 09:12:00', TIMESTAMP '2026-03-02 18:45:00', TIMESTAMP '2026-03-02 10:10:00', TIMESTAMP '2026-03-02 17:55:00', TIMESTAMP '2026-03-02 18:55:00', FALSE, FALSE, NULL, 'Doctor heavy day'),
        (7, DATE '2026-03-18', 'Field Work', 'MOB', TIMESTAMP '2026-03-18 09:18:00', TIMESTAMP '2026-03-18 18:40:00', TIMESTAMP '2026-03-18 10:25:00', TIMESTAMP '2026-03-18 17:50:00', TIMESTAMP '2026-03-18 19:00:00', FALSE, FALSE, 'Pooja Chouhan', 'Manager accompanied calls'),
        (7, DATE '2026-04-08', 'Other', 'API GRV', TIMESTAMP '2026-04-08 09:30:00', TIMESTAMP '2026-04-08 13:10:00', NULL, NULL, TIMESTAMP '2026-04-08 13:30:00', FALSE, FALSE, NULL, 'Other admin assignment'),

        (8, DATE '2026-01-08', 'Field Work', 'MOB', TIMESTAMP '2026-01-08 09:20:00', TIMESTAMP '2026-01-08 18:10:00', TIMESTAMP '2026-01-08 10:00:00', TIMESTAMP '2026-01-08 17:25:00', TIMESTAMP '2026-01-08 18:20:00', FALSE, FALSE, NULL, 'Pali route'),
        (8, DATE '2026-01-15', 'Field Work', 'MOB', TIMESTAMP '2026-01-15 09:22:00', TIMESTAMP '2026-01-15 18:18:00', TIMESTAMP '2026-01-15 10:05:00', TIMESTAMP '2026-01-15 17:05:00', TIMESTAMP '2026-01-15 18:30:00', FALSE, FALSE, NULL, 'Regular calls'),
        (8, DATE '2026-02-11', 'Leave', 'API GRV', TIMESTAMP '2026-02-11 09:00:00', TIMESTAMP '2026-02-11 09:00:00', NULL, NULL, TIMESTAMP '2026-02-11 09:05:00', FALSE, FALSE, NULL, 'Medical leave'),
        (8, DATE '2026-03-09', 'Field Work', 'IOS', TIMESTAMP '2026-03-09 09:28:00', TIMESTAMP '2026-03-09 18:40:00', TIMESTAMP '2026-03-09 10:14:00', TIMESTAMP '2026-03-09 17:55:00', TIMESTAMP '2026-03-10 08:00:00', TRUE, FALSE, NULL, 'Late submission'),
        (8, DATE '2026-04-02', 'Field Work', 'MOB', TIMESTAMP '2026-04-02 09:11:00', TIMESTAMP '2026-04-02 18:20:00', TIMESTAMP '2026-04-02 10:01:00', TIMESTAMP '2026-04-02 17:41:00', TIMESTAMP '2026-04-02 18:25:00', FALSE, FALSE, NULL, 'Final seeded day'),

        (10, DATE '2026-01-07', 'Field Work', 'MOB', TIMESTAMP '2026-01-07 09:00:00', TIMESTAMP '2026-01-07 18:25:00', TIMESTAMP '2026-01-07 10:30:00', TIMESTAMP '2026-01-07 17:35:00', TIMESTAMP '2026-01-07 18:30:00', FALSE, FALSE, NULL, 'Manager field coaching'),
        (10, DATE '2026-01-21', 'Admin Work', 'API GRV', TIMESTAMP '2026-01-21 09:30:00', TIMESTAMP '2026-01-21 17:00:00', NULL, NULL, TIMESTAMP '2026-01-21 17:10:00', FALSE, FALSE, NULL, 'Admin review'),
        (10, DATE '2026-02-06', 'Field Work', 'MOB', TIMESTAMP '2026-02-06 09:05:00', TIMESTAMP '2026-02-06 18:10:00', TIMESTAMP '2026-02-06 10:12:00', TIMESTAMP '2026-02-06 17:20:00', TIMESTAMP '2026-02-06 18:18:00', FALSE, TRUE, NULL, 'Off-route visit'),
        (10, DATE '2026-02-24', 'Conference', 'API GRV', TIMESTAMP '2026-02-24 10:00:00', TIMESTAMP '2026-02-24 16:00:00', NULL, NULL, TIMESTAMP '2026-02-24 16:05:00', FALSE, FALSE, NULL, 'Conference day'),
        (10, DATE '2026-03-12', 'Field Work', 'IOS', TIMESTAMP '2026-03-12 09:22:00', TIMESTAMP '2026-03-12 18:40:00', TIMESTAMP '2026-03-12 10:20:00', TIMESTAMP '2026-03-12 17:58:00', TIMESTAMP '2026-03-12 18:50:00', FALSE, FALSE, NULL, 'Retail support'),
        (10, DATE '2026-04-03', 'Field Work', 'MOB', TIMESTAMP '2026-04-03 09:17:00', TIMESTAMP '2026-04-03 18:22:00', TIMESTAMP '2026-04-03 10:18:00', TIMESTAMP '2026-04-03 17:45:00', TIMESTAMP '2026-04-03 18:27:00', FALSE, FALSE, 'Kanani Shubham', 'Joint work with MR'),

        (14, DATE '2026-01-09', 'Field Work', 'MOB', TIMESTAMP '2026-01-09 09:12:00', TIMESTAMP '2026-01-09 18:15:00', TIMESTAMP '2026-01-09 10:06:00', TIMESTAMP '2026-01-09 17:08:00', TIMESTAMP '2026-01-09 18:22:00', FALSE, FALSE, NULL, 'Rishikesh calls'),
        (14, DATE '2026-01-22', 'Field Work', 'MOB', TIMESTAMP '2026-01-22 09:09:00', TIMESTAMP '2026-01-22 18:30:00', TIMESTAMP '2026-01-22 10:02:00', TIMESTAMP '2026-01-22 17:52:00', TIMESTAMP '2026-01-22 18:40:00', FALSE, FALSE, NULL, 'Field work'),
        (14, DATE '2026-02-05', 'Chemist Work', 'IOS', TIMESTAMP '2026-02-05 09:15:00', TIMESTAMP '2026-02-05 17:40:00', TIMESTAMP '2026-02-05 10:30:00', TIMESTAMP '2026-02-05 16:10:00', TIMESTAMP '2026-02-05 17:50:00', FALSE, FALSE, NULL, 'Chemist focused day'),
        (14, DATE '2026-02-19', 'Field Work', 'MOB', TIMESTAMP '2026-02-19 09:18:00', TIMESTAMP '2026-02-19 18:42:00', TIMESTAMP '2026-02-19 10:25:00', TIMESTAMP '2026-02-19 17:49:00', TIMESTAMP '2026-02-19 18:48:00', FALSE, TRUE, NULL, 'Changed call route'),
        (14, DATE '2026-03-06', 'Stockist Work', 'API GRV', TIMESTAMP '2026-03-06 09:00:00', TIMESTAMP '2026-03-06 17:55:00', TIMESTAMP '2026-03-06 10:45:00', TIMESTAMP '2026-03-06 15:15:00', TIMESTAMP '2026-03-06 18:00:00', FALSE, FALSE, NULL, 'Stockist visit day'),
        (14, DATE '2026-03-20', 'Field Work', 'MOB', TIMESTAMP '2026-03-20 09:20:00', TIMESTAMP '2026-03-20 18:25:00', TIMESTAMP '2026-03-20 10:21:00', TIMESTAMP '2026-03-20 17:42:00', TIMESTAMP '2026-03-20 18:30:00', FALSE, FALSE, NULL, 'OPD visits'),
        (14, DATE '2026-04-04', 'Holiday', 'API GRV', TIMESTAMP '2026-04-04 09:00:00', TIMESTAMP '2026-04-04 09:00:00', NULL, NULL, TIMESTAMP '2026-04-04 09:03:00', FALSE, FALSE, NULL, 'Holiday')
)
INSERT INTO dcr_reports (
    employee_id,
    report_date,
    working_status,
    reported_from,
    day_start_time,
    day_end_time,
    first_call_time,
    last_call_time,
    submitted_at,
    is_delayed,
    is_deviated,
    joint_work_with,
    remark
)
SELECT
    e.id,
    s.report_date,
    s.working_status,
    s.reported_from,
    s.day_start_time,
    s.day_end_time,
    s.first_call_time,
    s.last_call_time,
    s.submitted_at,
    s.is_delayed,
    s.is_deviated,
    s.joint_work_with,
    s.remark
FROM seed_reports s
JOIN employees e ON e.id = s.employee_id
ON CONFLICT (employee_id, report_date) DO NOTHING;

WITH seed_calls (
    employee_id,
    report_date,
    doctor_id,
    provider_id,
    call_type,
    is_listed,
    party_name,
    is_in_person,
    pob_amount,
    call_time,
    remark
) AS (
    VALUES
        (7, DATE '2026-01-05', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Rajesh Patel', TRUE,  4200.00, TIMESTAMP '2026-01-05 10:05:00', 'Listed doctor call'),
        (7, DATE '2026-01-05', NULL, NULL, 'DOCTOR', FALSE, 'Dr. Hitesh Mehta', TRUE,  1800.00, TIMESTAMP '2026-01-05 11:25:00', 'Unlisted doctor call'),
        (7, DATE '2026-01-05', NULL, 6,    'CHEMIST', TRUE, 'Kanani medical',   TRUE,   650.00, TIMESTAMP '2026-01-05 15:10:00', 'Chemist visit'),
        (7, DATE '2026-01-06', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Neel Shah',    TRUE,  3000.00, TIMESTAMP '2026-01-06 10:15:00', 'Doctor call'),
        (7, DATE '2026-01-06', NULL, 7,    'STOCKIST', TRUE,'Patel Group',       TRUE,  1200.00, TIMESTAMP '2026-01-06 16:55:00', 'Stockist follow-up'),
        (7, DATE '2026-01-20', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Aakash Rana',   TRUE,  5400.00, TIMESTAMP '2026-01-20 10:20:00', 'Doctor POB'),
        (7, DATE '2026-01-20', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Vivek Joshi',   TRUE,  3500.00, TIMESTAMP '2026-01-20 12:10:00', 'Doctor POB'),
        (7, DATE '2026-01-20', NULL, 6,    'CHEMIST', TRUE, 'Kanani medical',    TRUE,   500.00, TIMESTAMP '2026-01-20 17:35:00', 'Chemist met'),
        (7, DATE '2026-02-03', NULL, NULL, 'DOCTOR', FALSE, 'Dr. Milan Desai',   TRUE,  2200.00, TIMESTAMP '2026-02-03 10:00:00', 'Unlisted doctor'),
        (7, DATE '2026-02-03', NULL, 7,    'STOCKIST', TRUE,'Patel Group',       TRUE,   900.00, TIMESTAMP '2026-02-03 17:40:00', 'Stockist call'),
        (7, DATE '2026-03-02', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Rohan Modi',    TRUE,  6300.00, TIMESTAMP '2026-03-02 10:10:00', 'Large doctor POB'),
        (7, DATE '2026-03-02', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Kavya Shah',    TRUE,  2400.00, TIMESTAMP '2026-03-02 13:10:00', 'Doctor POB'),
        (7, DATE '2026-03-18', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Hemal Dave',    TRUE,  4100.00, TIMESTAMP '2026-03-18 10:25:00', 'Manager visit'),
        (7, DATE '2026-03-18', NULL, 6,    'CHEMIST', TRUE, 'Kanani medical',    TRUE,   250.00, TIMESTAMP '2026-03-18 14:35:00', 'Chemist met'),

        (8, DATE '2026-01-08', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Pali One',      TRUE,  2100.00, TIMESTAMP '2026-01-08 10:00:00', 'Doctor visit'),
        (8, DATE '2026-01-08', NULL, 8,    'STOCKIST', TRUE,'Purohit Group',     TRUE,   700.00, TIMESTAMP '2026-01-08 16:15:00', 'Stockist follow-up'),
        (8, DATE '2026-01-15', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Pali Two',      TRUE,  1900.00, TIMESTAMP '2026-01-15 10:05:00', 'Doctor visit'),
        (8, DATE '2026-01-15', NULL, NULL, 'DOCTOR', FALSE, 'Dr. Unlisted Pali', TRUE,   850.00, TIMESTAMP '2026-01-15 13:00:00', 'Unlisted doctor'),
        (8, DATE '2026-03-09', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Pali Three',    TRUE,  2600.00, TIMESTAMP '2026-03-09 10:14:00', 'Doctor visit'),
        (8, DATE '2026-03-09', NULL, 8,    'STOCKIST', TRUE,'Purohit Group',     TRUE,   950.00, TIMESTAMP '2026-03-09 17:20:00', 'Stockist order'),
        (8, DATE '2026-04-02', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Pali Four',     TRUE,  1800.00, TIMESTAMP '2026-04-02 10:01:00', 'Doctor call'),

        (10, DATE '2026-01-07', NULL, NULL, 'DOCTOR', TRUE, 'Dr. Coach One',     TRUE,  1500.00, TIMESTAMP '2026-01-07 10:30:00', 'ASM coaching'),
        (10, DATE '2026-01-07', NULL, 6,    'CHEMIST', TRUE,'Kanani medical',    TRUE,   300.00, TIMESTAMP '2026-01-07 14:20:00', 'Chemist met'),
        (10, DATE '2026-02-06', NULL, NULL, 'DOCTOR', TRUE, 'Dr. Coach Two',     TRUE,  1750.00, TIMESTAMP '2026-02-06 10:12:00', 'Doctor met'),
        (10, DATE '2026-03-12', NULL, NULL, 'DOCTOR', TRUE, 'Dr. Retail One',    TRUE,  1100.00, TIMESTAMP '2026-03-12 10:20:00', 'Retail follow-up'),
        (10, DATE '2026-03-12', NULL, NULL, 'DOCTOR', FALSE,'Dr. Retail Two',    TRUE,   650.00, TIMESTAMP '2026-03-12 12:00:00', 'Unlisted doctor'),
        (10, DATE '2026-04-03', NULL, 7,    'STOCKIST', TRUE,'Patel Group',      TRUE,   800.00, TIMESTAMP '2026-04-03 17:10:00', 'Joint work stockist'),
        (10, DATE '2026-04-03', NULL, NULL, 'DOCTOR', TRUE, 'Dr. Coach Three',   TRUE,  1350.00, TIMESTAMP '2026-04-03 10:18:00', 'Doctor met'),

        (14, DATE '2026-01-09', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Rishi One',       TRUE, 2200.00, TIMESTAMP '2026-01-09 10:06:00', 'Doctor met'),
        (14, DATE '2026-01-09', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Rishi Two',       TRUE, 1800.00, TIMESTAMP '2026-01-09 12:20:00', 'Doctor met'),
        (14, DATE '2026-01-22', NULL, NULL, 'DOCTOR', FALSE, 'Dr. Unlisted Rishi',  TRUE,  900.00, TIMESTAMP '2026-01-22 10:02:00', 'Unlisted doctor'),
        (14, DATE '2026-02-05', NULL, 8,    'CHEMIST', TRUE, 'Purohit Group',       TRUE,  450.00, TIMESTAMP '2026-02-05 10:30:00', 'Chemist day'),
        (14, DATE '2026-02-19', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Harish Rawat',    TRUE, 2500.00, TIMESTAMP '2026-02-19 10:25:00', 'Doctor met'),
        (14, DATE '2026-03-06', NULL, 8,    'STOCKIST', TRUE,'Purohit Group',       TRUE,  850.00, TIMESTAMP '2026-03-06 10:45:00', 'Stockist day'),
        (14, DATE '2026-03-20', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Anuj Sethi',      TRUE, 2100.00, TIMESTAMP '2026-03-20 10:21:00', 'Doctor met'),
        (14, DATE '2026-03-20', NULL, NULL, 'DOCTOR', TRUE,  'Dr. Manish Katoch',   TRUE, 1750.00, TIMESTAMP '2026-03-20 13:35:00', 'Doctor met')
)
INSERT INTO dcr_report_calls (
    dcr_report_id,
    doctor_id,
    provider_id,
    call_type,
    is_listed,
    party_name,
    is_in_person,
    pob_amount,
    call_time,
    remark
)
SELECT
    r.id,
    CAST(s.doctor_id AS BIGINT),
    CAST(s.provider_id AS BIGINT),
    s.call_type,
    s.is_listed,
    s.party_name,
    s.is_in_person,
    s.pob_amount,
    s.call_time,
    s.remark
FROM seed_calls s
JOIN dcr_reports r
  ON r.employee_id = s.employee_id
 AND r.report_date = s.report_date
ON CONFLICT ON CONSTRAINT uq_dcr_report_calls_unique DO NOTHING;
