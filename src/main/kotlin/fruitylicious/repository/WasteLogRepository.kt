package fruitylicious.repository

import fruitylicious.entity.WasteLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WasteLogRepository : JpaRepository<WasteLogEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<WasteLogEntity>
}