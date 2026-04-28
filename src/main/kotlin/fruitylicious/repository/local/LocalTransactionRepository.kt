package fruitylicious.repository.local

import fruitylicious.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface LocalTransactionRepository : JpaRepository<Transaction, Long> {

    fun findAllByBranchId(branchId: Long): List<Transaction>

    fun findAllByBranchIdAndStatus(branchId: Long, status: String): List<Transaction>

    fun findAllByBranchIdAndDateTimeBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Transaction>

    fun findAllByDateTimeBetween(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Transaction>

    fun findAllByIsSyncedFalse(): List<Transaction>

    @Query(
        """
        SELECT COALESCE(SUM(t.totalAmount), 0)
        FROM Transaction t
        WHERE t.branchId = :branchId
          AND t.status = 'completed'
          AND t.dateTime BETWEEN :from AND :to
        """
    )
    fun sumRevenueByBranchAndPeriod(
        @Param("branchId") branchId: Long,
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime
    ): BigDecimal

    @Query(
        """
        SELECT COALESCE(SUM(t.totalAmount), 0)
        FROM Transaction t
        WHERE t.status = 'completed'
          AND t.dateTime BETWEEN :from AND :to
        """
    )
    fun sumRevenueAllBranchesAndPeriod(
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime
    ): BigDecimal

    @Query(
        """
        SELECT COUNT(t)
        FROM Transaction t
        WHERE t.branchId = :branchId
          AND t.status = :status
          AND t.dateTime BETWEEN :from AND :to
        """
    )
    fun countByBranchStatusAndPeriod(
        @Param("branchId") branchId: Long,
        @Param("status") status: String,
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime
    ): Long
}