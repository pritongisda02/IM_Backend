package fruitylicious.repository.local

import fruitylicious.entity.TransactionItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LocalTransactionItemRepository : JpaRepository<TransactionItem, Long> {

    fun findAllByTransactionId(transactionId: Long): List<TransactionItem>

    fun findAllByProductId(productId: Long): List<TransactionItem>

    fun findAllByIsSyncedFalse(): List<TransactionItem>

    @Query(
        """
        SELECT ti FROM TransactionItem ti
        JOIN Transaction t ON ti.transactionId = t.transactionId
        WHERE t.branchId = :branchId
          AND t.status = 'completed'
          AND t.dateTime BETWEEN :from AND :to
        """
    )
    fun findCompletedItemsByBranchAndPeriod(
        @Param("branchId") branchId: Long,
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime
    ): List<TransactionItem>

    @Query(
        """
        SELECT ti FROM TransactionItem ti
        JOIN Transaction t ON ti.transactionId = t.transactionId
        WHERE t.status = 'completed'
          AND t.dateTime BETWEEN :from AND :to
        """
    )
    fun findCompletedItemsAllBranchesAndPeriod(
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime
    ): List<TransactionItem>
}