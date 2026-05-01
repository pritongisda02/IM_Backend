package fruitylicious.repository

import fruitylicious.entity.SyncConflictEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SyncConflictRepository : JpaRepository<SyncConflictEntity, String> {

    fun findByBranchIdOrderByCreatedAtDesc(
        branchId: Int
    ): List<SyncConflictEntity>

    fun findAllByOrderByCreatedAtDesc(): List<SyncConflictEntity>
}
