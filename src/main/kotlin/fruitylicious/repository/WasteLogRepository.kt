package fruitylicious.repository

import fruitylicious.entity.WasteLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface WasteLogRepository : JpaRepository<WasteLogEntity, Long> {

    fun findAllByBranchBranchIdAndDateTimeBetween(
        branchId: Long,
        from: Instant,
        to: Instant
    ): List<WasteLogEntity>
}