package fruitylicious.repository

import fruitylicious.entity.TransactionItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionItemRepository : JpaRepository<TransactionItemEntity, Long> {

    fun findAllByTransactionTransactionId(transactionId: Long): List<TransactionItemEntity>
}