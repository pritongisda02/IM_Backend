package fruitylicious.repository

import fruitylicious.entity.AuditLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AuditLogRepository : JpaRepository<AuditLogEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<AuditLogEntity>
}