package fruitylicious.repository

import fruitylicious.entity.RestockLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface RestockLogRepository : JpaRepository<RestockLog, Long> {

    fun findAllByBranchBranchIdAndDateTimeBetween(
        branchId: Long,
        from: Instant,
        to: Instant
    ): List<RestockLog>
}