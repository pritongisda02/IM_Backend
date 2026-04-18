package fruitylicious.repository

import fruitylicious.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository <Transaction, Long> {
}