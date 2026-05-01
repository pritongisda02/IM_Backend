package fruitylicious.repository

import fruitylicious.entity.StaffLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface StaffLogRepository : JpaRepository<StaffLogEntity, Long> {

    fun findAllByBranchBranchIdAndClockInBetween(
        branchId: Long,
        from: Instant,
        to: Instant
    ): List<StaffLogEntity>
}