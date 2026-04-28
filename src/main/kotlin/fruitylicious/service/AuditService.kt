package fruitylicious.service

import fruitylicious.entity.AuditLog
import fruitylicious.repository.local.LocalAuditLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Centralised audit logging.
 * Called from every service that performs a write operation.
 * Uses REQUIRES_NEW so audit entries are never rolled back
 * by the calling transaction's failure.
 */
@Service
class AuditService(
    private val localAuditLogRepository: LocalAuditLogRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun log(
        userId: Long,
        branchId: Long,
        action: String,
        tableAffected: String,
        details: String? = null
    ) {
        val entry = AuditLog().apply {
            this.userId        = userId
            this.branchId      = branchId
            this.action        = action
            this.tableAffected = tableAffected
            this.details       = details
            this.timestamp     = LocalDateTime.now()
            this.lastModified  = LocalDateTime.now()
            this.isSynced      = false
        }
        localAuditLogRepository.save(entry)
    }
}

/**
 * Well-known audit action constants.
 * Using constants prevents typos across service calls.
 */
object AuditAction {
    // Products
    const val ADD_PRODUCT    = "ADD_PRODUCT"
    const val UPDATE_PRODUCT = "UPDATE_PRODUCT"
    const val DELETE_PRODUCT = "DELETE_PRODUCT"

    // Ingredients
    const val ADD_INGREDIENT    = "ADD_INGREDIENT"
    const val UPDATE_INGREDIENT = "UPDATE_INGREDIENT"
    const val DELETE_INGREDIENT = "DELETE_INGREDIENT"

    // Recipes
    const val ADD_RECIPE    = "ADD_RECIPE"
    const val UPDATE_RECIPE = "UPDATE_RECIPE"
    const val DELETE_RECIPE = "DELETE_RECIPE"

    // Inventory
    const val RESTOCK_INGREDIENT  = "RESTOCK_INGREDIENT"
    const val ADJUST_INVENTORY    = "ADJUST_INVENTORY"

    // Transactions
    const val CREATE_TRANSACTION = "CREATE_TRANSACTION"
    const val VOID_TRANSACTION   = "VOID_TRANSACTION"

    // Waste
    const val LOG_WASTE = "LOG_WASTE"

    // Staff
    const val CLOCK_IN  = "CLOCK_IN"
    const val CLOCK_OUT = "CLOCK_OUT"

    // Users
    const val ADD_USER    = "ADD_USER"
    const val UPDATE_USER = "UPDATE_USER"
    const val DELETE_USER = "DELETE_USER"
}