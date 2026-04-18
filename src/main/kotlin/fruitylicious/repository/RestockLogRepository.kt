package fruitylicious.repository

import fruitylicious.entity.RestockLog
import org.springframework.data.jpa.repository.JpaRepository

interface RestockLogRepository : JpaRepository<RestockLog, Long> {
}