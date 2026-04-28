package fruitylicious.repository.oracle

import fruitylicious.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface OracleTransactionRepository : JpaRepository<Transaction, Long> {

    fun findAllByBranchId(branchId: Long): List<Transaction>

    fun findAllByBranchIdAndStatus(branchId: Long, status: String): List<Transaction>

    fun findAllByLastModifiedAfter(lastModified: LocalDateTime): List<Transaction>

    fun findAllByBranchIdAndDateTimeBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Transaction>

    fun findAllByDateTimeBetween(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Transaction>

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
}