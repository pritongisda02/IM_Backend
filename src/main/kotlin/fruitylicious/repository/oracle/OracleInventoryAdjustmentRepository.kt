package fruitylicious.repository.oracle

import fruitylicious.entity.WasteLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OracleWasteLogRepository : JpaRepository<WasteLog, Long> {

    fun findAllByBranchId(branchId: Long): List<WasteLog>

    fun findAllByLastModifiedAfter(lastModified: LocalDateTime): List<WasteLog>

    fun findAllByBranchIdAndDateTimeBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<WasteLog>

    fun findAllByDateTimeBetween(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<WasteLog>
}