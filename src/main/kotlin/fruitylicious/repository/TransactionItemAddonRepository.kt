package fruitylicious.repository

import fruitylicious.entity.TransactionItemAddonEntity
import fruitylicious.repository.report.TransactionAddonLineRow
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

    @Query(
        """
        SELECT COUNT(tia)
        FROM TransactionItemAddonEntity tia
        JOIN TransactionItemEntity ti
            ON tia.transactionItemId = ti.transactionItemId
        JOIN TransactionEntity t
            ON ti.transactionId = t.transactionId
        WHERE t.branchId = :branchId
        AND (
            tia.lastModified > :since
            OR ti.lastModified > :since
            OR t.lastModified > :since
        )
        """
    )
    fun countChangedByBranchSince(
        @Param("branchId") branchId: Int,
        @Param("since") since: Long
    ): Long

    @Query(
        """
        SELECT
            tia.transactionItemId AS transactionItemId,
            tia.addonProductId AS addonProductId,
            p.productName AS addonName,
            tia.quantity AS quantity,
            tia.subtotal AS subtotal
        FROM TransactionItemAddonEntity tia
        JOIN ProductEntity p ON tia.addonProductId = p.productId
        WHERE tia.transactionItemId IN :transactionItemIds
        ORDER BY tia.transactionItemId ASC
        """
    )
    fun getAddonLines(
        @Param("transactionItemIds") transactionItemIds: List<String>
    ): List<TransactionAddonLineRow>
}