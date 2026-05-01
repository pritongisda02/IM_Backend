package fruitylicious.repository

import fruitylicious.entity.StaffLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StaffLogRepository : JpaRepository<StaffLogEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<StaffLogEntity>
}