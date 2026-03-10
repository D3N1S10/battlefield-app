CREATE TABLE IF NOT EXISTS battlefield (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    width INT NOT NULL,
    height INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS battle_unit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battlefield_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    unit_type VARCHAR(50) NOT NULL,
    pos_x INT NOT NULL,
    pos_y INT NOT NULL,
    size_width INT NOT NULL DEFAULT 1,
    size_height INT NOT NULL DEFAULT 1,
    health INT NOT NULL DEFAULT 100,
    max_health INT NOT NULL DEFAULT 100,
    speed INT NOT NULL DEFAULT 0,
    attack_range INT NOT NULL DEFAULT 1,
    damage INT NOT NULL DEFAULT 10,
    direction VARCHAR(20) NOT NULL DEFAULT 'ALL',
    is_alive BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (battlefield_id) REFERENCES battlefield(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS static_object (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battlefield_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    object_type VARCHAR(50) NOT NULL,
    pos_x INT NOT NULL,
    pos_y INT NOT NULL,
    size_width INT NOT NULL DEFAULT 1,
    size_height INT NOT NULL DEFAULT 1,
    is_destructible BOOLEAN NOT NULL DEFAULT FALSE,
    health INT NOT NULL DEFAULT -1,
    FOREIGN KEY (battlefield_id) REFERENCES battlefield(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS combat_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battlefield_id BIGINT NOT NULL,
    attacker_id BIGINT,
    target_id BIGINT,
    target_type VARCHAR(20) NOT NULL,
    damage_dealt INT NOT NULL,
    is_destroyed BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (battlefield_id) REFERENCES battlefield(id) ON DELETE CASCADE
);
