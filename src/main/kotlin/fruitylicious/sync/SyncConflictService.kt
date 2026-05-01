package fruitylicious.sync

import fruitylicious.entity.SyncConflictEntity
import fruitylicious.repository.SyncConflictRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SyncConflictService(
    private val syncConflictRepository: SyncConflictRepository
) {

    fun logConflict(
        branchId: Int,
        tableName: String,
        recordId: String,
        reason: String
    ) {
        syncConflictRepository.save(
            SyncConflictEntity(
                conflictId = UUID.randomUUID().toString(),
                branchId = branchId,
                tableName = tableName,
                recordId = recordId,
                reason = reason,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    fun getConflicts(
        branchId: Int?
    ): List<SyncConflictDto> {
        val conflicts = if (branchId == null) {
            syncConflictRepository.findAllByOrderByCreatedAtDesc()
        } else {
            syncConflictRepository.findByBranchIdOrderByCreatedAtDesc(branchId)
        }

        return conflicts.map {
            SyncConflictDto(
                conflictId = it.conflictId,
                branchId = it.branchId,
                tableName = it.tableName,
                recordId = it.recordId,
                reason = it.reason,
                createdAt = it.createdAt
            )
        }
    }
}