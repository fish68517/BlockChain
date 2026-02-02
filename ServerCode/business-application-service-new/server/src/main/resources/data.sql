-- CollectorCoin Initial Data
-- This file initializes the Admin user and other required data

-- Create Admin user (password: mmoo20031003, BCrypt encoded)
-- Note: The password hash is generated using BCryptPasswordEncoder
INSERT INTO users (username, password, email, role, wallet_address, eth_balance, created_at, updated_at)
SELECT 'Tulip', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/HxOqmvAoY90jZxwx.tDHO', 'admin@collectorcoin.com', 'ADMIN', '0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266', 10000.0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'Tulip');

-- Update existing Tulip user to ADMIN if exists
UPDATE users SET role = 'ADMIN', 
    password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/HxOqmvAoY90jZxwx.tDHO'
WHERE username = 'Tulip';
