package fruitylicious.sync

data class SyncConflictDto(
    val conflictId: String,
    val branchId: Int,
    val tableName: String,
    val recordId: String,
    val reason: String,
    val createdAt: Long
)