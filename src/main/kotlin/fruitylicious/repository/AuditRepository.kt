package fruitylicious.repository

import fruitylicious.entity.AuditLog
import org.springframework.data.jpa.repository.JpaRepository

interface AuditRepository : JpaRepository<AuditLog, Long> {
}