CREATE TABLE IF NOT EXISTS dcr_reports (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    tour_program_day_id BIGINT,
    report_date DATE NOT NULL,
    working_status VARCHAR(50) NOT NULL,
    reported_from VARCHAR(30) NOT NULL DEFAULT 'MOB',
    day_start_time TIMESTAMP,
    day_end_time TIMESTAMP,
    first_call_time TIMESTAMP,
    last_call_time TIMESTAMP,
    submitted_at TIMESTAMP,
    is_delayed BOOLEAN NOT NULL DEFAULT FALSE,
    is_deviated BOOLEAN NOT NULL DEFAULT FALSE,
    joint_work_with VARCHAR(120),
    remark TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dcr_reports_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_dcr_reports_tour_program_day
        FOREIGN KEY (tour_program_day_id) REFERENCES tour_program_days(id) ON DELETE SET NULL,
    CONSTRAINT uq_dcr_reports_employee_date
        UNIQUE (employee_id, report_date)
);

CREATE INDEX IF NOT EXISTS idx_dcr_reports_employee_date
    ON dcr_reports(employee_id, report_date);

CREATE INDEX IF NOT EXISTS idx_dcr_reports_report_date
    ON dcr_reports(report_date);

CREATE INDEX IF NOT EXISTS idx_dcr_reports_delayed
    ON dcr_reports(is_delayed);

CREATE TABLE IF NOT EXISTS dcr_report_calls (
    id BIGSERIAL PRIMARY KEY,
    dcr_report_id BIGINT NOT NULL,
    doctor_id BIGINT,
    provider_id BIGINT,
    call_type VARCHAR(20) NOT NULL,
    is_listed BOOLEAN NOT NULL DEFAULT TRUE,
    party_name VARCHAR(255) NOT NULL,
    is_in_person BOOLEAN NOT NULL DEFAULT TRUE,
    pob_amount NUMERIC(14, 2) NOT NULL DEFAULT 0,
    call_time TIMESTAMP,
    remark TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dcr_report_calls_report
        FOREIGN KEY (dcr_report_id) REFERENCES dcr_reports(id) ON DELETE CASCADE,
    CONSTRAINT fk_dcr_report_calls_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL,
    CONSTRAINT fk_dcr_report_calls_provider
        FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE SET NULL,
    CONSTRAINT chk_dcr_report_calls_type
        CHECK (call_type IN ('DOCTOR', 'CHEMIST', 'STOCKIST')),
    CONSTRAINT uq_dcr_report_calls_unique
        UNIQUE (dcr_report_id, call_type, party_name, call_time)
);

CREATE INDEX IF NOT EXISTS idx_dcr_report_calls_report
    ON dcr_report_calls(dcr_report_id);

CREATE INDEX IF NOT EXISTS idx_dcr_report_calls_type
    ON dcr_report_calls(call_type);
