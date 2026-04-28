package fruitylicious.repository

import fruitylicious.entity.AuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface AuditLogRepository : JpaRepository<AuditLog, Long> {

    fun findAllByBranchBranchIdAndTimestampBetween(
        branchId: Long,
        from: Instant,
        to: Instant
    ): List<AuditLog>
}