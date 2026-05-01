package fruitylicious.repository

import fruitylicious.entity.StaffLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface StaffLogRepository : JpaRepository<StaffLog, Long> {

    fun findAllByBranchBranchIdAndClockInBetween(
        branchId: Long,
        from: Instant,
        to: Instant
    ): List<StaffLog>
}