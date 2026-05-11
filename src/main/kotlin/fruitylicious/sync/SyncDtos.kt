package fruitylicious.sync

import fruitylicious.entity.AuditLogEntity
import fruitylicious.entity.BranchEntity
import fruitylicious.entity.IngredientEntity
import fruitylicious.entity.InventoryAdjustmentEntity
import fruitylicious.entity.InventoryEntity
import fruitylicious.entity.ProductEntity
import fruitylicious.entity.ProductRecipeEntity
import fruitylicious.entity.ProductVariantEntity
import fruitylicious.entity.RestockLogEntity
import fruitylicious.entity.StaffLogEntity
import fruitylicious.entity.TransactionEntity
import fruitylicious.entity.TransactionItemAddonEntity
import fruitylicious.entity.TransactionItemEntity
import fruitylicious.entity.UserEntity
import fruitylicious.entity.WasteLogEntity

data class PushRequest(
    val branches: List<BranchEntity> = emptyList(),
    val users: List<UserEntity> = emptyList(),
    val products: List<ProductEntity> = emptyList(),
    val productVariants: List<ProductVariantEntity> = emptyList(),
    val ingredients: List<IngredientEntity> = emptyList(),
    val productRecipes: List<ProductRecipeEntity> = emptyList(),
    val inventory: List<InventoryEntity> = emptyList(),
    val restockLogs: List<RestockLogEntity> = emptyList(),
    val inventoryAdjustments: List<InventoryAdjustmentEntity> = emptyList(),
    val wasteLogs: List<WasteLogEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val transactionItems: List<TransactionItemEntity> = emptyList(),
    val transactionItemAddons: List<TransactionItemAddonEntity> = emptyList(),
    val staffLogs: List<StaffLogEntity> = emptyList(),
    val auditLogs: List<AuditLogEntity> = emptyList()
)

data class SyncRecordResult(
    val recordId: String,
    val success: Boolean,
    val error: String? = null
)

data class PushResponse(
    val branches: List<SyncRecordResult> = emptyList(),
    val users: List<SyncRecordResult> = emptyList(),
    val products: List<SyncRecordResult> = emptyList(),
    val productVariants: List<SyncRecordResult> = emptyList(),
    val ingredients: List<SyncRecordResult> = emptyList(),
    val productRecipes: List<SyncRecordResult> = emptyList(),
    val inventory: List<SyncRecordResult> = emptyList(),
    val restockLogs: List<SyncRecordResult> = emptyList(),
    val inventoryAdjustments: List<SyncRecordResult> = emptyList(),
    val wasteLogs: List<SyncRecordResult> = emptyList(),
    val transactions: List<SyncRecordResult> = emptyList(),
    val transactionItems: List<SyncRecordResult> = emptyList(),
    val transactionItemAddons: List<SyncRecordResult> = emptyList(),
    val staffLogs: List<SyncRecordResult> = emptyList(),
    val auditLogs: List<SyncRecordResult> = emptyList()
)

data class PullResponse(
    val since: Long,
    val serverTime: Long,
    val branches: List<BranchEntity> = emptyList(),
    val users: List<UserEntity> = emptyList(),
    val products: List<ProductEntity> = emptyList(),
    val productVariants: List<ProductVariantEntity> = emptyList(),
    val ingredients: List<IngredientEntity> = emptyList(),
    val productRecipes: List<ProductRecipeEntity> = emptyList(),
    val inventory: List<InventoryEntity> = emptyList(),
    val restockLogs: List<RestockLogEntity> = emptyList(),
    val inventoryAdjustments: List<InventoryAdjustmentEntity> = emptyList(),
    val wasteLogs: List<WasteLogEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val transactionItems: List<TransactionItemEntity> = emptyList(),
    val transactionItemAddons: List<TransactionItemAddonEntity> = emptyList(),
    val staffLogs: List<StaffLogEntity> = emptyList(),
    val auditLogs: List<AuditLogEntity> = emptyList()
)