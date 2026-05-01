package fruitylicious.repository

import fruitylicious.entity.RestockLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface RestockLogRepository : JpaRepository<RestockLogEntity, Long> {

    fun findAllByBranchBranchIdAndDateTimeBetween(
        branchId: Long,
        from: Instant,
        to: Instant
    ): List<RestockLogEntity>
}