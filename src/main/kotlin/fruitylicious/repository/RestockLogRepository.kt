package fruitylicious.repository

import fruitylicious.entity.RestockLog
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface RestockLogRepository : JpaRepository<RestockLog, Long> {
    fun findByDateTimeBetween(start: LocalDateTime, end: LocalDateTime): List<RestockLog>
}