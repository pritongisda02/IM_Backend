package fruitylicious.repository

import fruitylicious.entity.TransactionItemEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionItemRepository : JpaRepository<TransactionItemEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<TransactionItemEntity>
}