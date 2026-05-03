-- =========================================================
-- FRUITYLICIOUS POS CENTRAL ORACLE DATABASE
-- 03_triggers_procedures.sql
-- PL/SQL triggers, procedures, and exception handling
-- =========================================================


-- =========================================================
-- 1. TRIGGER: PREVENT NEGATIVE INVENTORY
-- =========================================================

CREATE OR REPLACE TRIGGER trg_inventory_no_negative
BEFORE INSERT OR UPDATE OF current_stock ON inventory
FOR EACH ROW
BEGIN
    IF :NEW.current_stock < 0 THEN
        RAISE_APPLICATION_ERROR(
            -20001,
            'Inventory stock cannot be negative.'
        );
    END IF;
END;
/


-- =========================================================
-- 2. TRIGGER: VALIDATE TRANSACTION TOTAL
-- =========================================================

CREATE OR REPLACE TRIGGER trg_transaction_total_valid
BEFORE INSERT OR UPDATE OF total_amount ON transactions
FOR EACH ROW
BEGIN
    IF :NEW.total_amount < 0 THEN
        RAISE_APPLICATION_ERROR(
            -20002,
            'Transaction total amount cannot be negative.'
        );
    END IF;
END;
/


-- =========================================================
-- 3. TRIGGER: AUDIT TRANSACTION STATUS CHANGES
-- NOTE:
-- Uses user_id and branch_id from the transaction row.
-- Creates an audit log when status changes.
-- =========================================================

CREATE OR REPLACE TRIGGER trg_audit_transaction_status
AFTER UPDATE OF status ON transactions
FOR EACH ROW
WHEN (OLD.status <> NEW.status)
BEGIN
    INSERT INTO audit_logs (
        log_id,
        user_id,
        branch_id,
        action,
        table_affected,
        timestamp,
        last_modified,
        is_synced,
        synced_at
    ) VALUES (
        SYS_GUID(),
        :NEW.user_id,
        :NEW.branch_id,
        'Transaction ' || :NEW.transaction_id ||
        ' status changed from ' || :OLD.status ||
        ' to ' || :NEW.status,
        'transactions',
        ROUND(
            (CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01')
            * 86400000
        ),
        ROUND(
            (CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01')
            * 86400000
        ),
        1,
        ROUND(
            (CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01')
            * 86400000
        )
    );
END;
/


-- =========================================================
-- 4. PROCEDURE: RESTOCK INGREDIENT
-- Purpose:
-- Adds stock to inventory and inserts restock log in one transaction.
-- Demonstrates PL/SQL transaction-safe business operation.
-- =========================================================

CREATE OR REPLACE PROCEDURE sp_restock_ingredient (
    p_restock_id      IN VARCHAR2,
    p_ingredient_id   IN NUMBER,
    p_branch_id       IN NUMBER,
    p_user_id         IN NUMBER,
    p_quantity_added  IN NUMBER,
    p_supplier        IN VARCHAR2
)
AS
    v_now NUMBER;
    v_count NUMBER;
BEGIN
    IF p_quantity_added <= 0 THEN
        RAISE_APPLICATION_ERROR(
            -20010,
            'Restock quantity must be greater than zero.'
        );
    END IF;

    v_now := ROUND(
        (CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01')
        * 86400000
    );

    SELECT COUNT(*)
    INTO v_count
    FROM inventory
    WHERE ingredient_id = p_ingredient_id
    AND branch_id = p_branch_id;

    IF v_count = 0 THEN
        INSERT INTO inventory (
            ingredient_id,
            branch_id,
            current_stock,
            last_modified,
            is_synced,
            synced_at
        ) VALUES (
            p_ingredient_id,
            p_branch_id,
            p_quantity_added,
            v_now,
            1,
            v_now
        );
    ELSE
        UPDATE inventory
        SET current_stock = current_stock + p_quantity_added,
            last_modified = v_now,
            is_synced = 1,
            synced_at = v_now
        WHERE ingredient_id = p_ingredient_id
        AND branch_id = p_branch_id;
    END IF;

    INSERT INTO restock_logs (
        restock_id,
        ingredient_id,
        branch_id,
        user_id,
        quantity_added,
        supplier,
        date_time,
        last_modified,
        is_synced,
        synced_at
    ) VALUES (
        p_restock_id,
        p_ingredient_id,
        p_branch_id,
        p_user_id,
        p_quantity_added,
        p_supplier,
        v_now,
        v_now,
        1,
        v_now
    );

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(
            -20011,
            'Restock record already exists.'
        );

    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(
            -20012,
            'Referenced ingredient, branch, or user was not found.'
        );

    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(
            -20013,
            'Failed to restock ingredient: ' || SQLERRM
        );
END;
/


-- =========================================================
-- 5. PROCEDURE: RECORD WASTE
-- Purpose:
-- Deducts stock and records waste log.
-- Demonstrates exception handling and inventory validation.
-- =========================================================

CREATE OR REPLACE PROCEDURE sp_record_waste (
    p_waste_id       IN VARCHAR2,
    p_ingredient_id  IN NUMBER,
    p_branch_id      IN NUMBER,
    p_user_id        IN NUMBER,
    p_quantity       IN NUMBER,
    p_reason         IN VARCHAR2,
    p_image          IN VARCHAR2 DEFAULT NULL
)
AS
    v_now NUMBER;
    v_current_stock NUMBER;
BEGIN
    IF p_quantity <= 0 THEN
        RAISE_APPLICATION_ERROR(
            -20020,
            'Waste quantity must be greater than zero.'
        );
    END IF;

    v_now := ROUND(
        (CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01')
        * 86400000
    );

    SELECT current_stock
    INTO v_current_stock
    FROM inventory
    WHERE ingredient_id = p_ingredient_id
    AND branch_id = p_branch_id
    FOR UPDATE;

    IF v_current_stock < p_quantity THEN
        RAISE_APPLICATION_ERROR(
            -20021,
            'Insufficient stock to record waste.'
        );
    END IF;

    UPDATE inventory
    SET current_stock = current_stock - p_quantity,
        last_modified = v_now,
        is_synced = 1,
        synced_at = v_now
    WHERE ingredient_id = p_ingredient_id
    AND branch_id = p_branch_id;

    INSERT INTO waste_logs (
        waste_id,
        ingredient_id,
        branch_id,
        user_id,
        quantity,
        image,
        reason,
        date_time,
        last_modified,
        is_synced,
        synced_at
    ) VALUES (
        p_waste_id,
        p_ingredient_id,
        p_branch_id,
        p_user_id,
        p_quantity,
        p_image,
        p_reason,
        v_now,
        v_now,
        1,
        v_now
    );

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(
            -20022,
            'Inventory item not found.'
        );

    WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(
            -20023,
            'Waste record already exists.'
        );

    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(
            -20024,
            'Failed to record waste: ' || SQLERRM
        );
END;
/


-- =========================================================
-- 6. PROCEDURE: ADJUST INVENTORY
-- Purpose:
-- Manually adjusts inventory and records adjustment log.
-- p_adjustment_amount may be positive or negative.
-- =========================================================

CREATE OR REPLACE PROCEDURE sp_adjust_inventory (
    p_adjustment_id      IN VARCHAR2,
    p_ingredient_id      IN NUMBER,
    p_branch_id          IN NUMBER,
    p_user_id            IN NUMBER,
    p_adjustment_amount  IN NUMBER,
    p_reason             IN VARCHAR2
)
AS
    v_now NUMBER;
    v_current_stock NUMBER;
    v_new_stock NUMBER;
BEGIN
    v_now := ROUND(
        (CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01')
        * 86400000
    );

    SELECT current_stock
    INTO v_current_stock
    FROM inventory
    WHERE ingredient_id = p_ingredient_id
    AND branch_id = p_branch_id
    FOR UPDATE;

    v_new_stock := v_current_stock + p_adjustment_amount;

    IF v_new_stock < 0 THEN
        RAISE_APPLICATION_ERROR(
            -20030,
            'Adjustment would result in negative inventory.'
        );
    END IF;

    UPDATE inventory
    SET current_stock = v_new_stock,
        last_modified = v_now,
        is_synced = 1,
        synced_at = v_now
    WHERE ingredient_id = p_ingredient_id
    AND branch_id = p_branch_id;

    INSERT INTO inventory_adjustments (
        adjustment_id,
        ingredient_id,
        branch_id,
        user_id,
        adjustment_amount,
        reason,
        date_time,
        last_modified,
        is_synced,
        synced_at
    ) VALUES (
        p_adjustment_id,
        p_ingredient_id,
        p_branch_id,
        p_user_id,
        p_adjustment_amount,
        p_reason,
        v_now,
        v_now,
        1,
        v_now
    );

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(
            -20031,
            'Inventory item not found.'
        );

    WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(
            -20032,
            'Inventory adjustment record already exists.'
        );

    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(
            -20033,
            'Failed to adjust inventory: ' || SQLERRM
        );
END;
/


-- =========================================================
-- 7. PROCEDURE: COMPLETE ORDER
-- Purpose:
-- Updates queue/order status to completed.
-- Demonstrates transaction status workflow.
-- =========================================================

CREATE OR REPLACE PROCEDURE sp_complete_order (
    p_transaction_id IN VARCHAR2
)
AS
    v_now NUMBER;
    v_status VARCHAR2(30);
BEGIN
    v_now := ROUND(
        (CAST(SYSTIMESTAMP AT TIME ZONE 'UTC' AS DATE) - DATE '1970-01-01')
        * 86400000
    );

    SELECT status
    INTO v_status
    FROM transactions
    WHERE transaction_id = p_transaction_id
    FOR UPDATE;

    IF v_status = 'void' THEN
        RAISE_APPLICATION_ERROR(
            -20040,
            'Voided transaction cannot be completed.'
        );
    END IF;

    UPDATE transactions
    SET status = 'completed',
        last_modified = v_now,
        is_synced = 1,
        synced_at = v_now
    WHERE transaction_id = p_transaction_id;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(
            -20041,
            'Transaction not found.'
        );

    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(
            -20042,
            'Failed to complete order: ' || SQLERRM
        );
END;
/


-- =========================================================
-- 8. VIEW: LOW STOCK ITEMS
-- Useful for notifications and reporting.
-- =========================================================

CREATE OR REPLACE VIEW vw_low_stock_items AS
SELECT
    b.branch_id,
    b.branch_name,
    i.ingredient_id,
    i.ingredient_name,
    i.unit_type,
    inv.current_stock,
    i.low_stock_threshold,
    CASE
        WHEN inv.current_stock <= i.low_stock_threshold THEN 'CRITICAL'
        WHEN inv.current_stock <= (i.low_stock_threshold * 2) THEN 'WARNING'
        ELSE 'OK'
    END AS stock_status
FROM inventory inv
INNER JOIN ingredients i
    ON inv.ingredient_id = i.ingredient_id
INNER JOIN branches b
    ON inv.branch_id = b.branch_id
WHERE inv.current_stock <= (i.low_stock_threshold * 2);
/