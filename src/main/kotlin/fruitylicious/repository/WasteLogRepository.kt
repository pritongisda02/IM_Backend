package fruitylicious.repository

import fruitylicious.entity.WasteLog
import org.springframework.data.jpa.repository.JpaRepository

interface WasteLogRepository : JpaRepository<WasteLog, Long> {
}