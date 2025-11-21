CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    contact_phone VARCHAR(50),
    email VARCHAR(255),
    address VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE meters (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meter_no VARCHAR(100) NOT NULL UNIQUE,
    customer_id BIGINT,
    status VARCHAR(50),
    location VARCHAR(255),
    installed_at DATETIME,
    CONSTRAINT fk_meter_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE meter_readings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meter_id BIGINT NOT NULL,
    reading_value DECIMAL(12,2) NOT NULL,
    reading_time DATETIME NOT NULL,
    period_label VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reading_meter FOREIGN KEY (meter_id) REFERENCES meters (id)
);

CREATE TABLE bills (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    meter_id BIGINT,
    reading_id BIGINT,
    amount DECIMAL(12,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_bill_meter FOREIGN KEY (meter_id) REFERENCES meters (id),
    CONSTRAINT fk_bill_reading FOREIGN KEY (reading_id) REFERENCES meter_readings (id)
);

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bill_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    method VARCHAR(50),
    paid_at DATETIME NOT NULL,
    remark VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_bill FOREIGN KEY (bill_id) REFERENCES bills (id)
);

CREATE TABLE work_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    meter_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    assigned_to VARCHAR(255),
    reported_at DATETIME NOT NULL,
    completed_at DATETIME,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_work_order_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_work_order_meter FOREIGN KEY (meter_id) REFERENCES meters (id)
);

CREATE INDEX idx_bill_status ON bills (status);
CREATE INDEX idx_work_order_status ON work_orders (status);
CREATE INDEX idx_meter_customer ON meters (customer_id);
