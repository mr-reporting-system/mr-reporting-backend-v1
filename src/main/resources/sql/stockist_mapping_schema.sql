CREATE TABLE IF NOT EXISTS employee_stockist_mappings (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    stockist_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employee_stockist_mapping_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_stockist_mapping_stockist
        FOREIGN KEY (stockist_id) REFERENCES providers(id) ON DELETE CASCADE,
    CONSTRAINT uq_employee_stockist_mapping UNIQUE (employee_id, stockist_id)
);

CREATE INDEX IF NOT EXISTS idx_employee_stockist_mapping_employee
    ON employee_stockist_mappings(employee_id);

CREATE INDEX IF NOT EXISTS idx_employee_stockist_mapping_stockist
    ON employee_stockist_mappings(stockist_id);
