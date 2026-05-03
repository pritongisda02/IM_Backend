-- =========================================================
-- FRUITYLICIOUS POS CENTRAL ORACLE DATABASE
-- 05_warehouse_schema.sql
-- Star schema for sales analytics / data warehouse demo
-- =========================================================


-- =========================================================
-- DROP TABLES IF RESETTING WAREHOUSE
-- Uncomment only when resetting.
-- =========================================================

-- DROP TABLE fact_sales CASCADE CONSTRAINTS;
-- DROP TABLE dim_payment CASCADE CONSTRAINTS;
-- DROP TABLE dim_staff CASCADE CONSTRAINTS;
-- DROP TABLE dim_product CASCADE CONSTRAINTS;
-- DROP TABLE dim_branch CASCADE CONSTRAINTS;
-- DROP TABLE dim_date CASCADE CONSTRAINTS;


-- =========================================================
-- DIMENSION: DATE
-- =========================================================

CREATE TABLE dim_date (
    date_key        NUMBER(8)      NOT NULL, -- format: YYYYMMDD
    full_date       DATE           NOT NULL,
    day_number      NUMBER(2)      NOT NULL,
    day_name        VARCHAR2(20)   NOT NULL,
    week_number     NUMBER(2)      NOT NULL,
    month_number    NUMBER(2)      NOT NULL,
    month_name      VARCHAR2(20)   NOT NULL,
    quarter_number  NUMBER(1)      NOT NULL,
    year_number     NUMBER(4)      NOT NULL,

    CONSTRAINT pk_dim_date PRIMARY KEY (date_key),
    CONSTRAINT uk_dim_date_full UNIQUE (full_date),
    CONSTRAINT chk_dim_date_day CHECK (day_number BETWEEN 1 AND 31),
    CONSTRAINT chk_dim_date_month CHECK (month_number BETWEEN 1 AND 12),
    CONSTRAINT chk_dim_date_quarter CHECK (quarter_number BETWEEN 1 AND 4)
);


-- =========================================================
-- DIMENSION: BRANCH
-- =========================================================

CREATE TABLE dim_branch (
    branch_key      NUMBER(10)     NOT NULL,
    branch_id       NUMBER(10)     NOT NULL,
    branch_name     VARCHAR2(100)  NOT NULL,
    address         VARCHAR2(255),
    contact_number  VARCHAR2(50),

    CONSTRAINT pk_dim_branch PRIMARY KEY (branch_key),
    CONSTRAINT uk_dim_branch_id UNIQUE (branch_id)
);


-- =========================================================
-- DIMENSION: PRODUCT
-- =========================================================

CREATE TABLE dim_product (
    product_key    NUMBER(10)     NOT NULL,
    product_id     NUMBER(10)     NOT NULL,
    product_name   VARCHAR2(150)  NOT NULL,
    is_addon       NUMBER(1)      DEFAULT 0 NOT NULL,

    CONSTRAINT pk_dim_product PRIMARY KEY (product_key),
    CONSTRAINT uk_dim_product_id UNIQUE (product_id),
    CONSTRAINT chk_dim_product_addon CHECK (is_addon IN (0, 1))
);


-- =========================================================
-- DIMENSION: STAFF
-- =========================================================

CREATE TABLE dim_staff (
    staff_key  NUMBER(10)     NOT NULL,
    user_id    NUMBER(10)     NOT NULL,
    name       VARCHAR2(150)  NOT NULL,
    role       VARCHAR2(30)   NOT NULL,
    username   VARCHAR2(100)  NOT NULL,

    CONSTRAINT pk_dim_staff PRIMARY KEY (staff_key),
    CONSTRAINT uk_dim_staff_user UNIQUE (user_id),
    CONSTRAINT chk_dim_staff_role CHECK (role IN ('admin', 'staff', 'owner'))
);


-- =========================================================
-- DIMENSION: PAYMENT
-- =========================================================

CREATE TABLE dim_payment (
    payment_key   NUMBER(10)    NOT NULL,
    payment_type  VARCHAR2(30)  NOT NULL,

    CONSTRAINT pk_dim_payment PRIMARY KEY (payment_key),
    CONSTRAINT uk_dim_payment_type UNIQUE (payment_type),
    CONSTRAINT chk_dim_payment_type CHECK (payment_type IN ('Cash', 'Gcash'))
);


-- =========================================================
-- FACT TABLE: SALES
-- Grain:
-- One row per transaction item line.
-- =========================================================

CREATE TABLE fact_sales (
    sales_key            NUMBER(19)    NOT NULL,
    transaction_id       VARCHAR2(64)  NOT NULL,
    transaction_item_id  VARCHAR2(64)  NOT NULL,

    date_key             NUMBER(8)     NOT NULL,
    branch_key           NUMBER(10)    NOT NULL,
    product_key          NUMBER(10)    NOT NULL,
    staff_key            NUMBER(10)    NOT NULL,
    payment_key          NUMBER(10)    NOT NULL,

    quantity             NUMBER(10)    NOT NULL,
    gross_sales          NUMBER(12,2)  NOT NULL,
    transaction_status   VARCHAR2(30)  NOT NULL,

    CONSTRAINT pk_fact_sales PRIMARY KEY (sales_key),

    CONSTRAINT fk_fact_sales_date FOREIGN KEY (date_key)
        REFERENCES dim_date(date_key),

    CONSTRAINT fk_fact_sales_branch FOREIGN KEY (branch_key)
        REFERENCES dim_branch(branch_key),

    CONSTRAINT fk_fact_sales_product FOREIGN KEY (product_key)
        REFERENCES dim_product(product_key),

    CONSTRAINT fk_fact_sales_staff FOREIGN KEY (staff_key)
        REFERENCES dim_staff(staff_key),

    CONSTRAINT fk_fact_sales_payment FOREIGN KEY (payment_key)
        REFERENCES dim_payment(payment_key),

    CONSTRAINT chk_fact_sales_quantity CHECK (quantity > 0),
    CONSTRAINT chk_fact_sales_gross CHECK (gross_sales >= 0),
    CONSTRAINT chk_fact_sales_status CHECK (
        transaction_status IN ('pending', 'preparing', 'ready', 'completed', 'void')
    )
);


-- =========================================================
-- WAREHOUSE INDEXES
-- =========================================================

CREATE INDEX idx_fact_sales_date
ON fact_sales(date_key);

CREATE INDEX idx_fact_sales_branch
ON fact_sales(branch_key);

CREATE INDEX idx_fact_sales_product
ON fact_sales(product_key);

CREATE INDEX idx_fact_sales_staff
ON fact_sales(staff_key);

CREATE INDEX idx_fact_sales_payment
ON fact_sales(payment_key);

CREATE INDEX idx_fact_sales_status
ON fact_sales(transaction_status);

CREATE INDEX idx_fact_sales_date_branch
ON fact_sales(date_key, branch_key);

CREATE INDEX idx_fact_sales_product_date
ON fact_sales(product_key, date_key);


-- =========================================================
-- SAMPLE DIMENSION LOAD
-- For demo purposes only.
-- =========================================================

INSERT INTO dim_payment (
    payment_key,
    payment_type
) VALUES (
    1,
    'Cash'
);

INSERT INTO dim_payment (
    payment_key,
    payment_type
) VALUES (
    2,
    'Gcash'
);


-- Load branches from operational table
INSERT INTO dim_branch (
    branch_key,
    branch_id,
    branch_name,
    address,
    contact_number
)
SELECT
    branch_id AS branch_key,
    branch_id,
    branch_name,
    address,
    contact_number
FROM branches;


-- Load products from operational table
INSERT INTO dim_product (
    product_key,
    product_id,
    product_name,
    is_addon
)
SELECT
    product_id AS product_key,
    product_id,
    product_name,
    is_addon
FROM products;


-- Load staff/users from operational table
INSERT INTO dim_staff (
    staff_key,
    user_id,
    name,
    role,
    username
)
SELECT
    user_id AS staff_key,
    user_id,
    name,
    role,
    username
FROM users;


-- =========================================================
-- SAMPLE DATE DIMENSION LOAD
-- Inserts dates from today - 30 days to today.
-- =========================================================

BEGIN
    FOR i IN 0..30 LOOP
        INSERT INTO dim_date (
            date_key,
            full_date,
            day_number,
            day_name,
            week_number,
            month_number,
            month_name,
            quarter_number,
            year_number
        )
        SELECT
            TO_NUMBER(TO_CHAR(TRUNC(SYSDATE) - i, 'YYYYMMDD')),
            TRUNC(SYSDATE) - i,
            TO_NUMBER(TO_CHAR(TRUNC(SYSDATE) - i, 'DD')),
            TO_CHAR(TRUNC(SYSDATE) - i, 'DAY'),
            TO_NUMBER(TO_CHAR(TRUNC(SYSDATE) - i, 'IW')),
            TO_NUMBER(TO_CHAR(TRUNC(SYSDATE) - i, 'MM')),
            TO_CHAR(TRUNC(SYSDATE) - i, 'MONTH'),
            TO_NUMBER(TO_CHAR(TRUNC(SYSDATE) - i, 'Q')),
            TO_NUMBER(TO_CHAR(TRUNC(SYSDATE) - i, 'YYYY'))
        FROM dual
        WHERE NOT EXISTS (
            SELECT 1
            FROM dim_date
            WHERE date_key = TO_NUMBER(TO_CHAR(TRUNC(SYSDATE) - i, 'YYYYMMDD'))
        );
    END LOOP;
END;
/


-- =========================================================
-- SAMPLE FACT LOAD FROM OPERATIONAL TABLES
-- Loads completed transaction item lines into fact_sales.
-- For demo only; in production this can be scheduled ETL.
-- =========================================================

INSERT INTO fact_sales (
    sales_key,
    transaction_id,
    transaction_item_id,
    date_key,
    branch_key,
    product_key,
    staff_key,
    payment_key,
    quantity,
    gross_sales,
    transaction_status
)
SELECT
    ROW_NUMBER() OVER (ORDER BY t.date_time, ti.transaction_item_id) AS sales_key,
    t.transaction_id,
    ti.transaction_item_id,
    TO_NUMBER(TO_CHAR(
        DATE '1970-01-01' + (t.date_time / 86400000),
        'YYYYMMDD'
    )) AS date_key,
    t.branch_id AS branch_key,
    ti.product_id AS product_key,
    t.user_id AS staff_key,
    CASE
        WHEN t.payment_type = 'Cash' THEN 1
        WHEN t.payment_type = 'Gcash' THEN 2
        ELSE 1
    END AS payment_key,
    ti.quantity,
    ti.subtotal,
    t.status
FROM transactions t
INNER JOIN transaction_items ti
    ON t.transaction_id = ti.transaction_id
WHERE t.status = 'completed';

COMMIT;