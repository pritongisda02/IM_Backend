-- =========================================================
-- FRUITYLICIOUS POS CENTRAL ORACLE DATABASE
-- 04_sample_data.sql
-- Realistic sample data for demo/testing
-- =========================================================

-- Use current epoch milliseconds
-- Repeated inline where needed:
-- ROUND((========-- ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
-- BRANCHES
-- =========================================================

INSERT INTO branches (
    branch_id, branch_name, address, contact_number,
    last_modified, is_synced, synced_at
) VALUES (
    1, 'Branch 1', 'Diliman, Quezon City', '0917-111-0001',
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO branches (
    branch_id, branch_name, address, contact_number,
    last_modified, is_synced, synced_at
) VALUES (
    2, 'Branch 2', 'Cubao, Quezon City', '0917-222-0002',
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

-- =========================================================
-- USERS
-- NOTE:
-- Password values should be BCrypt hashes in production.
-- These sample hashes can be replaced by Spring Boot DataSeeder.
-- =========================================================

INSERT INTO users (
    user_id, name, role, username, password,
    last_modified, is_synced, synced_at
) VALUES (
    1, 'Default Admin', 'admin', 'admin',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhiZPj0BbqR0.l6Y0vBBSM4NQqU7Aa',
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO users (
    user_id, name, role, username, password,
    last_modified, is_synced, synced_at
) VALUES (
    2, 'Default Staff', 'staff', 'staff',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhiZPj0BbqR0.l6Y0vBBSM4NQqU7Aa',
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO users (
    user_id, name, role, username, password,
    last_modified, is_synced, synced_at
) VALUES (
    3, 'Branch 2 Staff', 'staff', 'staffb2',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhiZPj0BbqR0.l6Y0vBBSM4NQqU7Aa',
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

-- =========================================================
-- PRODUCTS
-- =========================================================

INSERT INTO products VALUES (
    1, NULL, 'Mango Shake', 0, 0,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO products VALUES (
    2, NULL, 'Avocado Shake', 0, 0,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO products VALUES (
    3, NULL, 'Melon Shake', 0, 0,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO products VALUES (
    4, NULL, 'Pearl', 1, 10,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO products VALUES (
    5, NULL, 'Oreo', 1, 15,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

-- =========================================================
-- PRODUCT VARIANTS
-- =========================================================

INSERT INTO product_variants VALUES (
    1, 1, 'Small', 60,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO product_variants VALUES (
    2, 1, 'Medium', 75,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO product_variants VALUES (
    3, 2, 'Small', 70,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO product_variants VALUES (
    4, 2, 'Medium', 85,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO product_variants VALUES (
    5, 3, 'Large', 90,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

-- =========================================================
-- INGREDIENTS
-- =========================================================

INSERT INTO ingredients VALUES (
    1, NULL, 'Mango', 'kg', 0, 0, 5,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000),
    1,
    ROUND((CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01') * 86400000)
);

INSERT INTO ingredients VALUES (


