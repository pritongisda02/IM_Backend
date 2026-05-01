package fruitylicious.repository

import fruitylicious.entity.TransactionItemAddonEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionItemAddonRepository : JpaRepository<TransactionItemAddonEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<TransactionItemAddonEntity>
}