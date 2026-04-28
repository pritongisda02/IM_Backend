package fruitylicious.repository.oracle

import fruitylicious.entity.RestockLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OracleRestockLogRepository : JpaRepository<RestockLog, Long> {

    fun findAllByBranchId(branchId: Long): List<RestockLog>

    fun findAllByLastModifiedAfter(lastModified: LocalDateTime): List<RestockLog>

    fun findAllByBranchIdAndDateTimeBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<RestockLog>
}