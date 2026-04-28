package fruitylicious.repository.local

import fruitylicious.entity.WasteLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LocalWasteLogRepository : JpaRepository<WasteLog, Long> {

    fun findAllByBranchId(branchId: Long): List<WasteLog>

    fun findAllByBranchIdAndIngredientId(branchId: Long, ingredientId: Long): List<WasteLog>

    fun findAllByBranchIdAndDateTimeBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<WasteLog>

    fun findAllByDateTimeBetween(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<WasteLog>

    fun findAllByIsSyncedFalse(): List<WasteLog>
}