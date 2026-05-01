package fruitylicious.repository

import fruitylicious.entity.TransactionItemAddonEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TransactionItemAddonRepository : JpaRepository<TransactionItemAddonEntity, String> {

    fun findByLastModifiedGreaterThan(lastModified: Long): List<TransactionItemAddonEntity>

    @Query(
        """
        SELECT tia
        FROM TransactionItemAddonEntity tia
        JOIN TransactionItemEntity ti ON tia.transactionItemId = ti.transactionItemId
        JOIN TransactionEntity t ON ti.transactionId = t.transactionId
        WHERE t.branchId = :branchId
        AND (
            tia.lastModified > :since
            OR ti.lastModified > :since
            OR t.lastModified > :since
        )
        """
    )
    fun findChangedByBranchSince(
        @Param("branchId") branchId: Int,
        @Param("since") since: Long
    ): List<TransactionItemAddonEntity>
}