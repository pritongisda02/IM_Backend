package fruitylicious.repository

import fruitylicious.entity.TransactionEntity
import fruitylicious.repository.report.PaymentBreakdownRow
import fruitylicious.repository.report.SalesSummaryRow
import fruitylicious.repository.report.TransactionReportRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TransactionRepository : JpaRepository<TransactionEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<TransactionEntity>
    fun findByBranchIdAndLastModifiedGreaterThan(branchId: Int, lastModified: Long): List<TransactionEntity>

    @Query(
        """
        SELECT 
            COALESCE(SUM(t.totalAmount), 0) AS totalSales,
            COUNT(t.transactionId) AS totalTransactions
        FROM TransactionEntity t
        WHERE t.status = 'completed'
        AND t.dateTime BETWEEN :from AND :to
        AND (:branchId IS NULL OR t.branchId = :branchId)
        """
    )
    fun getSalesSummary(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): SalesSummaryRow

    @Query(
        """
        SELECT 
            t.paymentType AS paymentType,
            COUNT(t.transactionId) AS transactionCount,
            COALESCE(SUM(t.totalAmount), 0) AS totalAmount
        FROM TransactionEntity t
        WHERE t.status = 'completed'
        AND t.dateTime BETWEEN :from AND :to
        AND (:branchId IS NULL OR t.branchId = :branchId)
        GROUP BY t.paymentType
        ORDER BY totalAmount DESC
        """
    )
    fun getPaymentBreakdown(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): List<PaymentBreakdownRow>

    @Query(
        """
    SELECT
        t.transactionId AS transactionId,
        t.userId AS userId,
        u.name AS userName,
        t.branchId AS branchId,
        t.totalAmount AS totalAmount,
        t.paymentType AS paymentType,
        t.dateTime AS dateTime,
        t.status AS status
    FROM TransactionEntity t
    JOIN UserEntity u ON t.userId = u.userId
    WHERE t.branchId = :branchId
    AND t.dateTime BETWEEN :from AND :to
    ORDER BY t.dateTime DESC
    """
    )
    fun getTransactionReportRows(
        @Param("branchId") branchId: Int,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): List<TransactionReportRow>

    @Query(
        """
    SELECT
        t.transactionId AS transactionId,
        t.userId AS userId,
        u.name AS userName,
        t.branchId AS branchId,
        t.totalAmount AS totalAmount,
        t.paymentType AS paymentType,
        t.dateTime AS dateTime,
        t.status AS status
    FROM TransactionEntity t
    JOIN UserEntity u ON t.userId = u.userId
    WHERE t.dateTime BETWEEN :from AND :to
    ORDER BY t.dateTime DESC
    """
    )
    fun getAllTransactionReportRows(
        @Param("from") from: Long,
        @Param("to") to: Long
    ): List<TransactionReportRow>

    fun countByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): Long
}