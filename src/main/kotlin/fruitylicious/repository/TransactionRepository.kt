package fruitylicious.repository

import fruitylicious.entity.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, Long> {

    fun findAllByBranchBranchIdAndDateTimeBetween(
        branchId: Long,
        from: Instant,
        to: Instant
    ): List<TransactionEntity>

    @Query("""
        SELECT COALESCE(SUM(t.totalAmount), 0)
        FROM Transaction t
        WHERE t.branch.branchId = :branchId
          AND t.dateTime BETWEEN :from AND :to
          AND t.status = 'completed'
    """)
    fun sumTotalAmountByBranchAndDateRange(
        @Param("branchId") branchId: Long,
        @Param("from") from: Instant,
        @Param("to") to: Instant
    ): BigDecimal

    @Query("""
        SELECT COALESCE(SUM(t.totalAmount), 0)
        FROM Transaction t
        WHERE t.dateTime BETWEEN :from AND :to
          AND t.status = 'completed'
    """)
    fun sumTotalAmountAllBranches(
        @Param("from") from: Instant,
        @Param("to") to: Instant
    ): BigDecimal
}