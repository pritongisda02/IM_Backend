package fruitylicious.repository.local

import fruitylicious.entity.RestockLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LocalRestockLogRepository : JpaRepository<RestockLog, Long> {

    fun findAllByBranchId(branchId: Long): List<RestockLog>

    fun findAllByBranchIdAndIngredientId(branchId: Long, ingredientId: Long): List<RestockLog>

    fun findAllByBranchIdAndDateTimeBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<RestockLog>

    fun findAllByDateTimeBetween(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<RestockLog>

    fun findAllByIsSyncedFalse(): List<RestockLog>
}