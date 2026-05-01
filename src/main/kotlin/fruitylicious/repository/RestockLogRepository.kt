package fruitylicious.repository

import fruitylicious.entity.RestockLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RestockLogRepository : JpaRepository<RestockLogEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<RestockLogEntity>
}