package fruitylicious.repository

import fruitylicious.entity.TransactionItemEntity
import fruitylicious.repository.report.SalesItemRow
import fruitylicious.repository.report.TransactionLineRow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TransactionItemRepository : JpaRepository<TransactionItemEntity, String> {

    fun findByLastModifiedGreaterThan(lastModified: Long): List<TransactionItemEntity>

    @Query(
        """
        SELECT 
            p.productId AS productId,
            p.productName AS productName,
            COALESCE(SUM(ti.quantity), 0) AS quantitySold,
            COALESCE(SUM(ti.subtotal), 0) AS grossSales
        FROM TransactionItemEntity ti
        JOIN TransactionEntity t ON ti.transactionId = t.transactionId
        JOIN ProductEntity p ON ti.productId = p.productId
        WHERE t.status = 'completed'
        AND t.dateTime BETWEEN :from AND :to
        AND (:branchId IS NULL OR t.branchId = :branchId)
        GROUP BY p.productId, p.productName
        ORDER BY grossSales DESC
        """
    )
    fun getSalesItems(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): List<SalesItemRow>

    @Query(
        value = """
            SELECT 
                p.productId AS productId,
                p.productName AS productName,
                COALESCE(SUM(ti.quantity), 0) AS quantitySold,
                COALESCE(SUM(ti.subtotal), 0) AS grossSales
            FROM TransactionItemEntity ti
            JOIN TransactionEntity t ON ti.transactionId = t.transactionId
            JOIN ProductEntity p ON ti.productId = p.productId
            WHERE t.status = 'completed'
            AND t.dateTime BETWEEN :from AND :to
            AND (:branchId IS NULL OR t.branchId = :branchId)
            GROUP BY p.productId, p.productName
            ORDER BY grossSales DESC
        """,
        countQuery = """
            SELECT COUNT(DISTINCT p.productId)
            FROM TransactionItemEntity ti
            JOIN TransactionEntity t ON ti.transactionId = t.transactionId
            JOIN ProductEntity p ON ti.productId = p.productId
            WHERE t.status = 'completed'
            AND t.dateTime BETWEEN :from AND :to
            AND (:branchId IS NULL OR t.branchId = :branchId)
        """
    )
    fun getSalesItemsPage(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long,
        pageable: Pageable
    ): Page<SalesItemRow>

    @Query(
        """
        SELECT
            ti.transactionId AS transactionId,
            p.productId AS productId,
            p.productName AS productName,
            ti.quantity AS quantity,
            ti.subtotal AS subtotal,
            ti.sizeName AS sizeName
        FROM TransactionItemEntity ti
        JOIN ProductEntity p ON ti.productId = p.productId
        WHERE ti.transactionId IN :transactionIds
        ORDER BY ti.transactionId ASC
        """
    )
    fun getTransactionLines(
        @Param("transactionIds") transactionIds: List<String>
    ): List<TransactionLineRow>

    @Query(
        """
        SELECT ti
        FROM TransactionItemEntity ti
        JOIN TransactionEntity t ON ti.transactionId = t.transactionId
        WHERE t.branchId = :branchId
        AND (
            ti.lastModified > :since
            OR t.lastModified > :since
        )
        """
    )
    fun findChangedByBranchSince(
        @Param("branchId") branchId: Int,
        @Param("since") since: Long
    ): List<TransactionItemEntity>

    @Query(
        """
        SELECT COUNT(ti)
        FROM TransactionItemEntity ti
        JOIN TransactionEntity t ON ti.transactionId = t.transactionId
        WHERE t.branchId = :branchId
        AND (
            ti.lastModified > :since
            OR t.lastModified > :since
        )
        """
    )
    fun countChangedByBranchSince(
        @Param("branchId") branchId: Int,
        @Param("since") since: Long
    ): Long
}
