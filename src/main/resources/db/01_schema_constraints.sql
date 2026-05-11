-- =========================================================
-- FRUITYLICIOUS POS CENTRAL ORACLE DATABASE SCHEMA
-- 01_schema_constraints.sql
-- =================================================-- =========================================================
-- Uncomment only if resetting schema.
-- DROP TABLE audit_logs CASCADE CONSTRAINTS;
-- DROP TABLE staff_logs CASCADE CONSTRAINTS;
-- DROP TABLE transaction_item_addons CASCADE CONSTRAINTS;
-- DROP TABLE transaction_items CASCADE CONSTRAINTS;
-- DROP TABLE transactions CASCADE CONSTRAINTS;
-- DROP TABLE inventory_adjustments CASCADE CONSTRAINTS;
-- DROP TABLE waste_logs CASCADE CONSTRAINTS;
-- DROP TABLE restock_logs CASCADE CONSTRAINTS;
-- DROP TABLE inventory CASCADE CONSTRAINTS;
-- DROP TABLE product_recipes CASCADE CONSTRAINTS;
-- DROP TABLE product_variants CASCADE CONSTRAINTS;
-- DROP TABLE products CASCADE CONSTRAINTS;
-- DROP TABLE ingredients CASCADE CONSTRAINTS;
-- DROP TABLE users CASCADE CONSTRAINTS;
-- DROP TABLE branches CASCADE CONSTRAINTS;

-- =========================================================
-- BRANCHES
-- =========================================================

CREATE TABLE branches (
    branch_id       NUMBER(10)      NOT NULL,
    branch_name     VARCHAR2(100)   NOT NULL,
    address         VARCHAR2(255)   NOT NULL,
    contact_number  VARCHAR2(50)    NOT NULL,
    last_modified   NUMBER(19)      NOT NULL,
    is_synced       NUMBER(1)       DEFAULT 1 NOT NULL,
    synced_at       NUMBER(19),

    CONSTRAINT pk_branches PRIMARY KEY (branch_id),
    CONSTRAINT uk_branches_name UNIQUE (branch_name),
    CONSTRAINT chk_branches_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- USERS
-- =========================================================

CREATE TABLE users (
    user_id        NUMBER(10)      NOT NULL,
    name           VARCHAR2(150)   NOT NULL,
    role           VARCHAR2(30)    NOT NULL,
    username       VARCHAR2(100)   NOT NULL,
    password       VARCHAR2(255)   NOT NULL,
    last_modified  NUMBER(19)      NOT NULL,
    is_synced      NUMBER(1)       DEFAULT 1 NOT NULL,
    synced_at      NUMBER(19),

    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT chk_users_role CHECK (role IN ('admin', 'staff', 'owner')),
    CONSTRAINT chk_users_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- PRODUCTS
-- =========================================================

CREATE TABLE products (
    product_id     NUMBER(10)      NOT NULL,
    image          VARCHAR2(500),
    product_name   VARCHAR2(150)   NOT NULL,
    is_addon       NUMBER(1)       DEFAULT 0 NOT NULL,
    price          NUMBER(12,2)    DEFAULT 0 NOT NULL,
    last_modified  NUMBER(19)      NOT NULL,
    is_synced      NUMBER(1)       DEFAULT 1 NOT NULL,
    synced_at      NUMBER(19),

    CONSTRAINT pk_products PRIMARY KEY (product_id),
    CONSTRAINT uk_products_name UNIQUE (product_name),
    CONSTRAINT chk_products_is_addon CHECK (is_addon IN (0, 1)),
    CONSTRAINT chk_products_price CHECK (price >= 0),
    CONSTRAINT chk_products_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- PRODUCT VARIANTS
-- =========================================================

CREATE TABLE product_variants (
    variant_id     NUMBER(10)      NOT NULL,
    product_id     NUMBER(10)      NOT NULL,
    size_name      VARCHAR2(50)    NOT NULL,
    price          NUMBER(12,2)    NOT NULL,
    last_modified  NUMBER(19)      NOT NULL,
    is_synced      NUMBER(1)       DEFAULT 1 NOT NULL,
    synced_at      NUMBER(19),

    CONSTRAINT pk_product_variants PRIMARY KEY (variant_id),
    CONSTRAINT fk_variants_product FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE CASCADE,
    CONSTRAINT uk_product_variant_size UNIQUE (product_id, size_name),
    CONSTRAINT chk_variants_price CHECK (price >= 0),
    CONSTRAINT chk_variants_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- INGREDIENTS
-- =========================================================

CREATE TABLE ingredients (
    ingredient_id              NUMBER(10)      NOT NULL,
    image                      VARCHAR2(500),
    ingredient_name            VARCHAR2(150)   NOT NULL,
    unit_type                  VARCHAR2(30)    NOT NULL,
    estimated_weight_per_unit  NUMBER(12,4)    DEFAULT 0 NOT NULL,
    is_packaging               NUMBER(1)       DEFAULT 0 NOT NULL,
    low_stock_threshold        NUMBER(12,4)    DEFAULT 0 NOT NULL,
    last_modified              NUMBER(19)      NOT NULL,
    is_synced                  NUMBER(1)       DEFAULT 1 NOT NULL,
    synced_at                  NUMBER(19),

    CONSTRAINT pk_ingredients PRIMARY KEY (ingredient_id),
    CONSTRAINT uk_ingredients_name UNIQUE (ingredient_name),
    CONSTRAINT chk_ingredients_packaging CHECK (is_packaging IN (0, 1)),
    CONSTRAINT chk_ingredients_weight CHECK (estimated_weight_per_unit >= 0),
    CONSTRAINT chk_ingredients_low_stock CHECK (low_stock_threshold >= 0),
    CONSTRAINT chk_ingredients_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- PRODUCT RECIPES
-- =========================================================

CREATE TABLE product_recipes (
    recipe_id          NUMBER(10)    NOT NULL,
    product_id         NUMBER(10)    NOT NULL,
    variant_id         NUMBER(10),
    ingredient_id      NUMBER(10)    NOT NULL,
    quantity_required  NUMBER(12,4)  NOT NULL,
    last_modified      NUMBER(19)    NOT NULL,
    is_synced          NUMBER(1)     DEFAULT 1 NOT NULL,
    synced_at          NUMBER(19),

    CONSTRAINT pk_product_recipes PRIMARY KEY (recipe_id),
    CONSTRAINT fk_recipes_product FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_recipes_variant FOREIGN KEY (variant_id)
        REFERENCES product_variants(variant_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_recipes_ingredient FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(ingredient_id),
    CONSTRAINT chk_recipes_quantity CHECK (quantity_required > 0),
    CONSTRAINT chk_recipes_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- INVENTORY
-- =========================================================

CREATE TABLE inventory (
    ingredient_id   NUMBER(10)    NOT NULL,
    branch_id       NUMBER(10)    NOT NULL,
    current_stock   NUMBER(12,4)  DEFAULT 0 NOT NULL,
    last_modified   NUMBER(19)    NOT NULL,
    is_synced       NUMBER(1)     DEFAULT 1 NOT NULL,
    synced_at       NUMBER(19),

    CONSTRAINT pk_inventory PRIMARY KEY (ingredient_id, branch_id),
    CONSTRAINT fk_inventory_ingredient FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(ingredient_id),
    CONSTRAINT fk_inventory_branch FOREIGN KEY (branch_id)
        REFERENCES branches(branch_id),
    CONSTRAINT chk_inventory_stock CHECK (current_stock >= 0),
    CONSTRAINT chk_inventory_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- TRANSACTIONS
-- =========================================================

CREATE TABLE transactions (
    transaction_id  VARCHAR2(64)   NOT NULL,
    user_id         NUMBER(10)     NOT NULL,
    branch_id       NUMBER(10)     NOT NULL,
    total_amount    NUMBER(12,2)   NOT NULL,
    payment_type    VARCHAR2(30)   NOT NULL,
    date_time       NUMBER(19)     NOT NULL,
    status          VARCHAR2(30)   NOT NULL,
    last_modified   NUMBER(19)     NOT NULL,
    is_synced       NUMBER(1)      DEFAULT 1 NOT NULL,
    synced_at       NUMBER(19),

    CONSTRAINT pk_transactions PRIMARY KEY (transaction_id),
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT fk_transactions_branch FOREIGN KEY (branch_id)
        REFERENCES branches(branch_id),
    CONSTRAINT chk_transactions_total CHECK (total_amount >= 0),
    CONSTRAINT chk_transactions_payment CHECK (payment_type IN ('Cash', 'Gcash')),
    CONSTRAINT chk_transactions_status CHECK (
        status IN ('pending', 'preparing', 'ready', 'completed', 'void')
    ),
    CONSTRAINT chk_transactions_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- TRANSACTION ITEMS
-- =========================================================

CREATE TABLE transaction_items (
    transaction_item_id  VARCHAR2(64)   NOT NULL,
    transaction_id       VARCHAR2(64)   NOT NULL,
    product_id           NUMBER(10)     NOT NULL,
    variant_id           NUMBER(10),
    size_name            VARCHAR2(50),
    quantity             NUMBER(10)     NOT NULL,
    subtotal             NUMBER(12,2)   NOT NULL,
    last_modified        NUMBER(19)     NOT NULL,
    is_synced            NUMBER(1)      DEFAULT 1 NOT NULL,
    synced_at            NUMBER(19),

    CONSTRAINT pk_transaction_items PRIMARY KEY (transaction_item_id),
    CONSTRAINT fk_items_transaction FOREIGN KEY (transaction_id)
        REFERENCES transactions(transaction_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_items_product FOREIGN KEY (product_id)
        REFERENCES products(product_id),
    CONSTRAINT fk_items_variant FOREIGN KEY (variant_id)
        REFERENCES product_variants(variant_id)
        ON DELETE SET NULL,
    CONSTRAINT chk_items_quantity CHECK (quantity > 0),
    CONSTRAINT chk_items_subtotal CHECK (subtotal >= 0),
    CONSTRAINT chk_items_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- TRANSACTION ITEM ADDONS
-- =========================================================

CREATE TABLE transaction_item_addons (
    transaction_item_addon_id  VARCHAR2(64)   NOT NULL,
    transaction_item_id        VARCHAR2(64)   NOT NULL,
    addon_product_id           NUMBER(10)     NOT NULL,
    quantity                   NUMBER(10)     NOT NULL,
    subtotal                   NUMBER(12,2)   NOT NULL,
    last_modified              NUMBER(19)     NOT NULL,
    is_synced                  NUMBER(1)      DEFAULT 1 NOT NULL,
    synced_at                  NUMBER(19),

    CONSTRAINT pk_transaction_item_addons PRIMARY KEY (transaction_item_addon_id),
    CONSTRAINT fk_addons_item FOREIGN KEY (transaction_item_id)
        REFERENCES transaction_items(transaction_item_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_addons_product FOREIGN KEY (addon_product_id)
        REFERENCES products(product_id),
    CONSTRAINT chk_addons_quantity CHECK (quantity > 0),
    CONSTRAINT chk_addons_subtotal CHECK (subtotal >= 0),
    CONSTRAINT chk_addons_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- RESTOCK LOGS
-- =========================================================

CREATE TABLE restock_logs (
    restock_id      VARCHAR2(64)   NOT NULL,
    ingredient_id   NUMBER(10)     NOT NULL,
    branch_id       NUMBER(10)     NOT NULL,
    user_id         NUMBER(10)     NOT NULL,
    quantity_added  NUMBER(12,4)   NOT NULL,
    supplier        VARCHAR2(150)  NOT NULL,
    date_time       NUMBER(19)     NOT NULL,
    last_modified   NUMBER(19)     NOT NULL,
    is_synced       NUMBER(1)      DEFAULT 1 NOT NULL,
    synced_at       NUMBER(19),

    CONSTRAINT pk_restock_logs PRIMARY KEY (restock_id),
    CONSTRAINT fk_restock_ingredient FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(ingredient_id),
    CONSTRAINT fk_restock_branch FOREIGN KEY (branch_id)
        REFERENCES branches(branch_id),
    CONSTRAINT fk_restock_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT chk_restock_quantity CHECK (quantity_added > 0),
    CONSTRAINT chk_restock_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- WASTE LOGS
-- =========================================================

CREATE TABLE waste_logs (
    waste_id       VARCHAR2(64)   NOT NULL,
    ingredient_id  NUMBER(10)     NOT NULL,
    branch_id      NUMBER(10)     NOT NULL,
    user_id        NUMBER(10)     NOT NULL,
    quantity       NUMBER(12,4)   NOT NULL,
    image          VARCHAR2(500),
    reason         VARCHAR2(255)  NOT NULL,
    date_time      NUMBER(19)     NOT NULL,
    last_modified  NUMBER(19)     NOT NULL,
    is_synced      NUMBER(1)      DEFAULT 1 NOT NULL,
    synced_at      NUMBER(19),

    CONSTRAINT pk_waste_logs PRIMARY KEY (waste_id),
    CONSTRAINT fk_waste_ingredient FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(ingredient_id),
    CONSTRAINT fk_waste_branch FOREIGN KEY (branch_id)
        REFERENCES branches(branch_id),
    CONSTRAINT fk_waste_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT chk_waste_quantity CHECK (quantity > 0),
    CONSTRAINT chk_waste_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- INVENTORY ADJUSTMENTS
-- =========================================================

CREATE TABLE inventory_adjustments (
    adjustment_id      VARCHAR2(64)   NOT NULL,
    ingredient_id      NUMBER(10)     NOT NULL,
    branch_id          NUMBER(10)     NOT NULL,
    user_id            NUMBER(10)     NOT NULL,
    adjustment_amount  NUMBER(12,4)   NOT NULL,
    reason             VARCHAR2(255)  NOT NULL,
    date_time          NUMBER(19)     NOT NULL,
    last_modified      NUMBER(19)     NOT NULL,
    is_synced          NUMBER(1)      DEFAULT 1 NOT NULL,
    synced_at          NUMBER(19),

    CONSTRAINT pk_inventory_adjustments PRIMARY KEY (adjustment_id),
    CONSTRAINT fk_adjustments_ingredient FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(ingredient_id),
    CONSTRAINT fk_adjustments_branch FOREIGN KEY (branch_id)
        REFERENCES branches(branch_id),
    CONSTRAINT fk_adjustments_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT chk_adjustments_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- STAFF LOGS
-- =========================================================

CREATE TABLE staff_logs (
    log_id         VARCHAR2(64)   NOT NULL,
    user_id        NUMBER(10)     NOT NULL,
    branch_id      NUMBER(10)     NOT NULL,
    image          VARCHAR2(500),
    clock_in       NUMBER(19)     NOT NULL,
    clock_out      NUMBER(19),
    last_modified  NUMBER(19)     NOT NULL,
    is_synced      NUMBER(1)      DEFAULT 1 NOT NULL,
    synced_at      NUMBER(19),

    CONSTRAINT pk_staff_logs PRIMARY KEY (log_id),
    CONSTRAINT fk_staff_logs_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT fk_staff_logs_branch FOREIGN KEY (branch_id)
        REFERENCES branches(branch_id),
    CONSTRAINT chk_staff_log_time CHECK (clock_out IS NULL OR clock_out >= clock_in),
    CONSTRAINT chk_staff_logs_synced CHECK (is_synced IN (0, 1))
);

-- =========================================================
-- AUDIT LOGS
-- =========================================================

CREATE TABLE audit_logs (
    log_id          VARCHAR2(64)   NOT NULL,
    user_id         NUMBER(10)     NOT NULL,
    branch_id       NUMBER(10)     NOT NULL,
    action          VARCHAR2(500)  NOT NULL,
    table_affected  VARCHAR2(100)  NOT NULL,
    timestamp       NUMBER(19)     NOT NULL,
    last_modified   NUMBER(19)     NOT NULL,
    is_synced       NUMBER(1)      DEFAULT 1 NOT NULL,
    synced_at       NUMBER(19),

    CONSTRAINT pk_audit_logs PRIMARY KEY (log_id),
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT fk_audit_branch FOREIGN KEY (branch_id)
        REFERENCES branches(branch_id),
    CONSTRAINT chk_audit_synced CHECK (is_synced IN (0, 1))
);

CREATE TABLE sync_conflicts (
    conflict_id  VARCHAR2(64)    NOT NULL,
    branch_id    NUMBER(10)      NOT NULL,
    table_name   VARCHAR2(100)   NOT NULL,
    record_id    VARCHAR2(128)   NOT NULL,
    reason       VARCHAR2(1000)  NOT NULL,
    created_at   NUMBER(19)      NOT NULL,

    CONSTRAINT pk_sync_conflicts PRIMARY KEY (conflict_id),
    CONSTRAINT fk_sync_conflicts_branch FOREIGN KEY (branch_id)
        REFERENCES branches(branch_id)
);

