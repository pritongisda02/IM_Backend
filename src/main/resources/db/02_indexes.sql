-- =========================================================
-- FRUITYLICIOUS POS CENTRAL ORACLE DATABASE
-- 02_indexes.sql
-- Performance indexes for sync, reports, and lookups
-- =========================================================

-- =========================================================
-- USERS
-- =========================================================

CREATE INDEX idx_users_role
ON users(role);

CREATE INDEX idx_users_last_modified
ON users(last_modified);

-- username already has UNIQUE constraint, which creates an index.


-- =========================================================
-- BRANCHES
-- =========================================================

CREATE INDEX idx_branches_last_modified
ON branches(last_modified);


-- =========================================================
-- PRODUCTS
-- =========================================================

CREATE INDEX idx_products_name
ON products(product_name);

CREATE INDEX idx_products_is_addon
ON products(is_addon);

CREATE INDEX idx_products_last_modified
ON products(last_modified);


-- =========================================================
-- PRODUCT VARIANTS
-- =========================================================

CREATE INDEX idx_variants_product_id
ON product_variants(product_id);

CREATE INDEX idx_variants_last_modified
ON product_variants(last_modified);


-- =========================================================
-- INGREDIENTS
-- =========================================================

CREATE INDEX idx_ingredients_name
ON ingredients(ingredient_name);

CREATE INDEX idx_ingredients_is_packaging
ON ingredients(is_packaging);

CREATE INDEX idx_ingredients_last_modified
ON ingredients(last_modified);


-- =========================================================
-- PRODUCT RECIPES
-- =========================================================

CREATE INDEX idx_recipes_product_id
ON product_recipes(product_id);

CREATE INDEX idx_recipes_variant_id
ON product_recipes(variant_id);

CREATE INDEX idx_recipes_ingredient_id
ON product_recipes(ingredient_id);

CREATE INDEX idx_recipes_last_modified
ON product_recipes(last_modified);


-- =========================================================
-- INVENTORY
-- =========================================================

CREATE INDEX idx_inventory_branch_id
ON inventory(branch_id);

CREATE INDEX idx_inventory_ingredient_id
ON inventory(ingredient_id);

CREATE INDEX idx_inventory_stock
ON inventory(current_stock);

CREATE INDEX idx_inventory_last_modified
ON inventory(last_modified);

CREATE INDEX idx_inventory_branch_stock
ON inventory(branch_id, current_stock);


-- =========================================================
-- TRANSACTIONS
-- =========================================================

CREATE INDEX idx_transactions_user_id
ON transactions(user_id);

CREATE INDEX idx_transactions_branch_id
ON transactions(branch_id);

CREATE INDEX idx_transactions_date_time
ON transactions(date_time);

CREATE INDEX idx_transactions_status
ON transactions(status);

CREATE INDEX idx_transactions_payment_type
ON transactions(payment_type);

CREATE INDEX idx_transactions_last_modified
ON transactions(last_modified);

CREATE INDEX idx_transactions_branch_date
ON transactions(branch_id, date_time);

CREATE INDEX idx_transactions_branch_status_date
ON transactions(branch_id, status, date_time);

CREATE INDEX idx_transactions_status_date
ON transactions(status, date_time);


-- =========================================================
-- TRANSACTION ITEMS
-- =========================================================

CREATE INDEX idx_items_transaction_id
ON transaction_items(transaction_id);

CREATE INDEX idx_items_product_id
ON transaction_items(product_id);

CREATE INDEX idx_items_variant_id
ON transaction_items(variant_id);

CREATE INDEX idx_items_last_modified
ON transaction_items(last_modified);

CREATE INDEX idx_items_product_transaction
ON transaction_items(product_id, transaction_id);


-- =========================================================
-- TRANSACTION ITEM ADDONS
-- =========================================================

CREATE INDEX idx_addons_transaction_item_id
ON transaction_item_addons(transaction_item_id);

CREATE INDEX idx_addons_product_id
ON transaction_item_addons(addon_product_id);

CREATE INDEX idx_addons_last_modified
ON transaction_item_addons(last_modified);


-- =========================================================
-- RESTOCK LOGS
-- =========================================================

CREATE INDEX idx_restock_ingredient_id
ON restock_logs(ingredient_id);

CREATE INDEX idx_restock_branch_id
ON restock_logs(branch_id);

CREATE INDEX idx_restock_user_id
ON restock_logs(user_id);

CREATE INDEX idx_restock_date_time
ON restock_logs(date_time);

CREATE INDEX idx_restock_last_modified
ON restock_logs(last_modified);

CREATE INDEX idx_restock_branch_date
ON restock_logs(branch_id, date_time);


-- =========================================================
-- WASTE LOGS
-- =========================================================

CREATE INDEX idx_waste_ingredient_id
ON waste_logs(ingredient_id);

CREATE INDEX idx_waste_branch_id
ON waste_logs(branch_id);

CREATE INDEX idx_waste_user_id
ON waste_logs(user_id);

CREATE INDEX idx_waste_date_time
ON waste_logs(date_time);

CREATE INDEX idx_waste_reason
ON waste_logs(reason);

CREATE INDEX idx_waste_last_modified
ON waste_logs(last_modified);

CREATE INDEX idx_waste_branch_date
ON waste_logs(branch_id, date_time);


-- =========================================================
-- INVENTORY ADJUSTMENTS
-- =========================================================

CREATE INDEX idx_adjustments_ingredient_id
ON inventory_adjustments(ingredient_id);

CREATE INDEX idx_adjustments_branch_id
ON inventory_adjustments(branch_id);

CREATE INDEX idx_adjustments_user_id
ON inventory_adjustments(user_id);

CREATE INDEX idx_adjustments_date_time
ON inventory_adjustments(date_time);

CREATE INDEX idx_adjustments_last_modified
ON inventory_adjustments(last_modified);

CREATE INDEX idx_adjustments_branch_date
ON inventory_adjustments(branch_id, date_time);


-- =========================================================
-- STAFF LOGS
-- =========================================================

CREATE INDEX idx_staff_logs_user_id
ON staff_logs(user_id);

CREATE INDEX idx_staff_logs_branch_id
ON staff_logs(branch_id);

CREATE INDEX idx_staff_logs_clock_in
ON staff_logs(clock_in);

CREATE INDEX idx_staff_logs_clock_out
ON staff_logs(clock_out);

CREATE INDEX idx_staff_logs_last_modified
ON staff_logs(last_modified);

CREATE INDEX idx_staff_logs_branch_clock_in
ON staff_logs(branch_id, clock_in);

CREATE INDEX idx_staff_logs_user_clock_in
ON staff_logs(user_id, clock_in);


-- =========================================================
-- AUDIT LOGS
-- =========================================================

CREATE INDEX idx_audit_logs_user_id
ON audit_logs(user_id);

CREATE INDEX idx_audit_logs_branch_id
ON audit_logs(branch_id);

CREATE INDEX idx_audit_logs_action
ON audit_logs(action);

CREATE INDEX idx_audit_logs_table_affected
ON audit_logs(table_affected);

CREATE INDEX idx_audit_logs_timestamp
ON audit_logs(timestamp);

CREATE INDEX idx_audit_logs_last_modified
ON audit_logs(last_modified);

CREATE INDEX idx_audit_logs_branch_timestamp
ON audit_logs(branch_id, timestamp);


-- =========================================================
-- COMMON SYNC OPTIMIZATION
-- These indexes support:
-- SELECT * FROM table WHERE last_modified > :since
-- =========================================================

-- Already added per table:
-- idx_*_last_modified

CREATE INDEX idx_sync_conflicts_branch
ON sync_conflicts(branch_id);

CREATE INDEX idx_sync_conflicts_table
ON sync_conflicts(table_name);

CREATE INDEX idx_sync_conflicts_record
ON sync_conflicts(record_id);

CREATE INDEX idx_sync_conflicts_created
ON sync_conflicts(created_at);