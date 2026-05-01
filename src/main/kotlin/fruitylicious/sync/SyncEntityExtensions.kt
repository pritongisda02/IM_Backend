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

fun BranchEntity.copyForServer(serverTime: Long): BranchEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun UserEntity.copyForServer(serverTime: Long): UserEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun ProductEntity.copyForServer(serverTime: Long): ProductEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun ProductVariantEntity.copyForServer(serverTime: Long): ProductVariantEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun IngredientEntity.copyForServer(serverTime: Long): IngredientEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun ProductRecipeEntity.copyForServer(serverTime: Long): ProductRecipeEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun InventoryEntity.copyForServer(serverTime: Long): InventoryEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun RestockLogEntity.copyForServer(serverTime: Long): RestockLogEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun InventoryAdjustmentEntity.copyForServer(serverTime: Long): InventoryAdjustmentEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun WasteLogEntity.copyForServer(serverTime: Long): WasteLogEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun TransactionEntity.copyForServer(serverTime: Long): TransactionEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun TransactionItemEntity.copyForServer(serverTime: Long): TransactionItemEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun TransactionItemAddonEntity.copyForServer(serverTime: Long): TransactionItemAddonEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun StaffLogEntity.copyForServer(serverTime: Long): StaffLogEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}

fun AuditLogEntity.copyForServer(serverTime: Long): AuditLogEntity {
    isSynced = true
    syncedAt = serverTime
    return this
}