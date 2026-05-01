package fruitylicious.repository

import fruitylicious.entity.WasteLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface WasteLogRepository : JpaRepository<WasteLog, Long> {

    fun findAllByBranchBranchIdAndDateTimeBetween(
        branchId: Long,
        from: Instant,
        to: Instant
    ): List<WasteLog>
}