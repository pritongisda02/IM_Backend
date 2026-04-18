package fruitylicious.repository

import fruitylicious.entity.TransactionItem
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionItemRepository : JpaRepository <TransactionItem, Long> {
}