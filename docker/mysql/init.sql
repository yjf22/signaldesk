-- SignalDesk Database Initialization
-- This script runs when the MySQL container starts for the first time

CREATE DATABASE IF NOT EXISTS signaldesk
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Grant privileges to root from any host (development only)
GRANT ALL PRIVILEGES ON signaldesk.* TO 'root'@'%';
FLUSH PRIVILEGES;
