-- schema.sql
-- GlobalBooks Payments Service Database Schema

-- Create database (run this manually in PostgreSQL)
-- CREATE DATABASE globalbooks_payments;

-- Users table (for authentication)
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- User roles table
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
                                        payment_id VARCHAR(255) PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    description TEXT,
    external_transaction_id VARCHAR(255),
    failure_reason TEXT,
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_payments_customer_id ON payments(customer_id);
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_created_at ON payments(created_at);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, enabled)
VALUES ('admin', 'admin@globalbooks.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8p6/4PfWJ8m6.', true)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users WHERE username = 'admin'
    ON CONFLICT DO NOTHING;

-- Insert default test user (password: user123)
INSERT INTO users (username, email, password, enabled)
VALUES ('testuser', 'testuser@globalbooks.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_USER' FROM users WHERE username = 'testuser'
    ON CONFLICT DO NOTHING;

-- Sample payment data for testing
INSERT INTO payments (payment_id, order_id, customer_id, amount, currency, payment_method, status, description)
VALUES
    ('PAY-12345678', 'ORD-12345678', 'testuser', 59.98, 'USD', 'CREDIT_CARD', 'COMPLETED', 'Payment for books order'),
    ('PAY-87654321', 'ORD-87654321', 'testuser', 29.99, 'USD', 'PAYPAL', 'PENDING', 'Payment for single book order')
    ON CONFLICT (payment_id) DO NOTHING;

-- Update processed_at for completed payment
UPDATE payments SET processed_at = CURRENT_TIMESTAMP WHERE status = 'COMPLETED' AND processed_at IS NULL;

