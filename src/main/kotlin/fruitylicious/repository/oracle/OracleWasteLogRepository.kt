package fruitylicious.repository.oracle

import fruitylicious.entity.WasteLog
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface OracleWasteLogRepository : JpaRepository<WasteLog, Long> {
    fun findByDateTimeBetween(start: LocalDateTime, end: LocalDateTime): List<WasteLog>
}