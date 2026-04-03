-- Baseline schema for Delta Scientia.
-- Uses IF NOT EXISTS for idempotency on databases that already have tables.

CREATE TABLE IF NOT EXISTS variable_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    unit_of_measure VARCHAR(100),
    data_type VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    is_custom BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS experiment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    category VARCHAR(100),
    status VARCHAR(50),
    created_by VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS variable (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    experiment_id BIGINT NOT NULL,
    variable_type_id BIGINT,
    name VARCHAR(255) NOT NULL,
    unit_of_measure VARCHAR(100),
    data_type VARCHAR(50),
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_variable_experiment FOREIGN KEY (experiment_id) REFERENCES experiment(id),
    CONSTRAINT fk_variable_type FOREIGN KEY (variable_type_id) REFERENCES variable_type(id)
);

CREATE TABLE IF NOT EXISTS trial (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    experiment_id BIGINT NOT NULL,
    trial_number INT,
    label VARCHAR(255),
    notes TEXT,
    execution_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trial_experiment FOREIGN KEY (experiment_id) REFERENCES experiment(id)
);

CREATE TABLE IF NOT EXISTS trial_value (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trial_id BIGINT NOT NULL,
    variable_id BIGINT NOT NULL,
    value_text VARCHAR(4000),
    value_numeric DOUBLE,
    value_long_text TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trialvalue_trial FOREIGN KEY (trial_id) REFERENCES trial(id),
    CONSTRAINT fk_trialvalue_variable FOREIGN KEY (variable_id) REFERENCES variable(id)
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_variable_experiment_id ON variable(experiment_id);
CREATE INDEX IF NOT EXISTS idx_variable_variable_type_id ON variable(variable_type_id);
CREATE INDEX IF NOT EXISTS idx_variable_experiment_data_type ON variable(experiment_id, data_type);
CREATE INDEX IF NOT EXISTS idx_trial_experiment_id ON trial(experiment_id);
CREATE INDEX IF NOT EXISTS idx_trialvalue_trial_id ON trial_value(trial_id);
CREATE INDEX IF NOT EXISTS idx_trialvalue_variable_id ON trial_value(variable_id);
CREATE INDEX IF NOT EXISTS idx_variable_type_name ON variable_type(name);
CREATE INDEX IF NOT EXISTS idx_experiment_category ON experiment(category);
CREATE INDEX IF NOT EXISTS idx_experiment_status ON experiment(status);
