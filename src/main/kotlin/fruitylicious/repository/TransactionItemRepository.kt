package fruitylicious.repository

import fruitylicious.entity.TransactionItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionItemRepository : JpaRepository<TransactionItem, Long> {

    fun findAllByTransactionTransactionId(transactionId: Long): List<TransactionItem>
}