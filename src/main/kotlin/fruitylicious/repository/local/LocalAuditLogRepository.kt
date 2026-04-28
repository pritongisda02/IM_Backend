package fruitylicious.repository.local

import fruitylicious.entity.AuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LocalAuditLogRepository : JpaRepository<AuditLog, Long> {

    fun findAllByBranchId(branchId: Long): List<AuditLog>

    fun findAllByUserId(userId: Long): List<AuditLog>

    fun findAllByBranchIdAndAction(branchId: Long, action: String): List<AuditLog>

    fun findAllByBranchIdAndTimestampBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<AuditLog>

    fun findAllByTimestampBetween(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<AuditLog>

    fun findAllByTableAffected(tableAffected: String): List<AuditLog>

    fun findAllByIsSyncedFalse(): List<AuditLog>
}