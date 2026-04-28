package fruitylicious.dto

import java.time.LocalDateTime

enum class SyncPhase { PUSH, PULL, IDLE }
enum class SyncResult { SUCCESS, PARTIAL, FAILED, SKIPPED }

data class TableSyncSummary(
    val tableName: String,
    val recordsPushed: Int = 0,
    val recordsPulled: Int = 0,
    val failedPushes: Int = 0,
    val errors: List<String> = emptyList()
)

data class SyncStatusResponse(
    val lastSyncAttempt: LocalDateTime?,
    val lastSuccessfulSync: LocalDateTime?,
    val currentPhase: SyncPhase,
    val result: SyncResult,
    val totalUnsyncedRecords: Int,
    val tableSummaries: List<TableSyncSummary>,
    val online: Boolean,
    val message: String
)

data class SyncTriggerResponse(
    val triggered: Boolean,
    val message: String,
    val triggeredAt: LocalDateTime
)