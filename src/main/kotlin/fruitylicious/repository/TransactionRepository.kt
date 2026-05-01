package fruitylicious.repository

import fruitylicious.entity.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<TransactionEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<TransactionEntity>
    fun findByBranchIdAndLastModifiedGreaterThan(branchId: Int, lastModified: Long): List<TransactionEntity>
}