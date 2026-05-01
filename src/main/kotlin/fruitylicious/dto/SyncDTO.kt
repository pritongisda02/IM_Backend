package fruitylicious.dto

import java.time.Instant

// ── Push ──────────────────────────────────────────────────────────────────────

data class SyncPushRequest(
    val inventory: List<InventoryRequest> = emptyList(),
    val restockLogs: List<RestockLogRequest> = emptyList(),
    val inventoryAdjustments: List<InventoryAdjustmentRequest> = emptyList(),
    val wasteLogs: List<WasteLogRequest> = emptyList(),
    val transactions: List<TransactionRequest> = emptyList(),
    val transactionItems: List<TransactionItemRequest> = emptyList(),
    val auditLogs: List<AuditLogRequest> = emptyList(),
    val staffLogs: List<StaffLogRequest> = emptyList()
)

data class SyncRecordResult(
    val id: Long,
    val success: Boolean,
    val error: String? = null
)

data class SyncPushResponse(
    val inventory: List<SyncRecordResult> = emptyList(),
    val restockLogs: List<SyncRecordResult> = emptyList(),
    val inventoryAdjustments: List<SyncRecordResult> = emptyList(),
    val wasteLogs: List<SyncRecordResult> = emptyList(),
    val transactions: List<SyncRecordResult> = emptyList(),
    val transactionItems: List<SyncRecordResult> = emptyList(),
    val auditLogs: List<SyncRecordResult> = emptyList(),
    val staffLogs: List<SyncRecordResult> = emptyList()
)

// ── Pull ──────────────────────────────────────────────────────────────────────

data class SyncPullResponse(
    val since: Instant,
    val branches: List<BranchResponse> = emptyList(),
    val users: List<UserResponse> = emptyList(),
    val products: List<ProductResponse> = emptyList(),
    val ingredients: List<IngredientResponse> = emptyList(),
    val recipes: List<RecipeResponse> = emptyList()
)