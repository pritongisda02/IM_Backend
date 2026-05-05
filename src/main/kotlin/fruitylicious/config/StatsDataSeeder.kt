package fruitylicious.config

import fruitylicious.entity.AuditLogEntity
import fruitylicious.entity.RestockLogEntity
import fruitylicious.entity.StaffLogEntity
import fruitylicious.entity.TransactionEntity
import fruitylicious.entity.TransactionItemAddonEntity
import fruitylicious.entity.TransactionItemEntity
import fruitylicious.entity.UserEntity
import fruitylicious.entity.WasteLogEntity
import fruitylicious.repository.AuditLogRepository
import fruitylicious.repository.BranchRepository
import fruitylicious.repository.ProductRepository
import fruitylicious.repository.ProductVariantRepository
import fruitylicious.repository.RestockLogRepository
import fruitylicious.repository.StaffLogRepository
import fruitylicious.repository.TransactionItemAddonRepository
import fruitylicious.repository.TransactionItemRepository
import fruitylicious.repository.TransactionRepository
import fruitylicious.repository.UserRepository
import fruitylicious.repository.WasteLogRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.roundToInt
import kotlin.random.Random

@Component
@Order(2)
@ConditionalOnProperty(
    prefix = "fruitylicious.seed.stats",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class StatsDataSeeder(
    private val branchRepository: BranchRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val productVariantRepository: ProductVariantRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionItemRepository: TransactionItemRepository,
    private val transactionItemAddonRepository: TransactionItemAddonRepository,
    private val restockLogRepository: RestockLogRepository,
    private val wasteLogRepository: WasteLogRepository,
    private val staffLogRepository: StaffLogRepository,
    private val auditLogRepository: AuditLogRepository,

    @Value("\${fruitylicious.seed.stats.start-date:2026-03-01}")
    private val startDateText: String,

    @Value("\${fruitylicious.seed.stats.end-date:}")
    private val endDateText: String
) : ApplicationRunner {

    private val zoneId: ZoneId = ZoneId.of("Asia/Manila")
    private val random = Random(20260301)

    @Transactional
    override fun run(args: ApplicationArguments) {
        seedDefaultUsersIfMissing()

        val startDate = LocalDate.parse(startDateText)
        val endDate = if (endDateText.isBlank()) {
            LocalDate.now(zoneId)
        } else {
            LocalDate.parse(endDateText)
        }

        if (endDate.isBefore(startDate)) {
            return
        }

        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            seedDailyStaffLogs(currentDate)
            seedDailyTransactions(currentDate)
            seedPeriodicRestocks(currentDate)
            seedPeriodicWaste(currentDate)

            currentDate = currentDate.plusDays(1)
        }
    }

    private fun seedDefaultUsersIfMissing() {
        val now = System.currentTimeMillis()

        val users = listOf(
            UserEntity(
                userId = 1,
                name = "Default Admin",
                role = "admin",
                username = "admin",
                password = "admin123",
                isDeleted = false,
                deletedAt = null,
                lastModified = now,
                isSynced = true,
                syncedAt = now
            ),
            UserEntity(
                userId = 2,
                name = "Branch 1 Staff",
                role = "staff",
                username = "staff1",
                password = "staff123",
                isDeleted = false,
                deletedAt = null,
                lastModified = now,
                isSynced = true,
                syncedAt = now
            ),
            UserEntity(
                userId = 3,
                name = "Branch 2 Staff",
                role = "staff",
                username = "staff2",
                password = "staff123",
                isDeleted = false,
                deletedAt = null,
                lastModified = now,
                isSynced = true,
                syncedAt = now
            )
        )

        users.forEach { user ->
            if (!userRepository.existsById(user.userId)) {
                userRepository.save(user)
            }
        }
    }

    private fun seedDailyStaffLogs(date: LocalDate) {
        val dateKey = dateKey(date)

        val staffLogs = listOf(
            StaffSeed(
                logId = "STAT-SL-$dateKey-B1-U2",
                userId = 2,
                branchId = 1,
                clockIn = epochMillis(date, 8, 0),
                clockOut = epochMillis(date, 17, 0)
            ),
            StaffSeed(
                logId = "STAT-SL-$dateKey-B2-U3",
                userId = 3,
                branchId = 2,
                clockIn = epochMillis(date, 8, 30),
                clockOut = epochMillis(date, 17, 30)
            )
        )

        staffLogs.forEach { seed ->
            if (!staffLogRepository.existsById(seed.logId)) {
                staffLogRepository.save(
                    StaffLogEntity(
                        logId = seed.logId,
                        userId = seed.userId,
                        branchId = seed.branchId,
                        image = null,
                        clockIn = seed.clockIn,
                        clockOut = seed.clockOut,
                        lastModified = seed.clockOut,
                        isSynced = true,
                        syncedAt = seed.clockOut
                    )
                )
            }
        }
    }

    private fun seedDailyTransactions(date: LocalDate) {
        val branchIds = listOf(1, 2)

        branchIds.forEach { branchId ->
            if (!branchRepository.existsById(branchId)) {
                return@forEach
            }

            val userId = if (branchId == 1) 2 else 3
            val orderCount = random.nextInt(7, 15)

            for (orderNo in 1..orderCount) {
                seedSingleTransaction(
                    date = date,
                    branchId = branchId,
                    userId = userId,
                    orderNo = orderNo
                )
            }
        }
    }

    private fun seedSingleTransaction(
        date: LocalDate,
        branchId: Int,
        userId: Int,
        orderNo: Int
    ) {
        val dateKey = dateKey(date)
        val transactionId = "STAT-TX-$dateKey-B$branchId-${orderNo.toString().padStart(3, '0')}"

        if (transactionRepository.existsById(transactionId)) {
            return
        }

        val itemCount = random.nextInt(1, 4)
        val selectedItems = mutableListOf<GeneratedTransactionItem>()
        var transactionTotal = 0.0

        repeat(itemCount) { index ->
            val menuItem = menuProducts.random(random)
            val isLarge = random.nextDouble() < 0.35
            val variantId = if (isLarge) {
                menuItem.largeVariantId
            } else {
                menuItem.mediumVariantId
            }

            val variant = productVariantRepository.findById(variantId).orElse(null)
                ?: return@repeat

            val quantity = random.nextInt(1, 4)
            val selectedAddons = pickRandomAddons()
            val addonSubtotal = selectedAddons.sumOf { it.price * quantity }
            val lineSubtotal = (variant.price * quantity) + addonSubtotal

            selectedItems.add(
                GeneratedTransactionItem(
                    transactionItemId = "$transactionId-ITEM-${(index + 1).toString().padStart(2, '0')}",
                    productId = menuItem.productId,
                    variantId = variant.variantId,
                    sizeName = variant.sizeName,
                    quantity = quantity,
                    subtotal = lineSubtotal,
                    addons = selectedAddons
                )
            )

            transactionTotal += lineSubtotal
        }

        if (selectedItems.isEmpty()) {
            return
        }

        val hour = random.nextInt(9, 20)
        val minute = random.nextInt(0, 60)
        val transactionTime = epochMillis(date, hour, minute)
        val paymentType = if (random.nextDouble() < 0.65) "Cash" else "Gcash"

        transactionRepository.save(
            TransactionEntity(
                transactionId = transactionId,
                userId = userId,
                branchId = branchId,
                totalAmount = roundMoney(transactionTotal),
                paymentType = paymentType,
                dateTime = transactionTime,
                status = "completed",
                lastModified = transactionTime,
                isSynced = true,
                syncedAt = transactionTime
            )
        )

        selectedItems.forEach { item ->
            transactionItemRepository.save(
                TransactionItemEntity(
                    transactionItemId = item.transactionItemId,
                    transactionId = transactionId,
                    productId = item.productId,
                    variantId = item.variantId,
                    sizeName = item.sizeName,
                    quantity = item.quantity,
                    subtotal = roundMoney(item.subtotal),
                    lastModified = transactionTime,
                    isSynced = true,
                    syncedAt = transactionTime
                )
            )

            item.addons.forEachIndexed { addonIndex, addon ->
                val addonId = "${item.transactionItemId}-ADDON-${(addonIndex + 1).toString().padStart(2, '0')}"

                transactionItemAddonRepository.save(
                    TransactionItemAddonEntity(
                        transactionItemAddonId = addonId,
                        transactionItemId = item.transactionItemId,
                        addonProductId = addon.productId,
                        quantity = item.quantity,
                        subtotal = roundMoney(addon.price * item.quantity),
                        lastModified = transactionTime,
                        isSynced = true,
                        syncedAt = transactionTime
                    )
                )
            }
        }

        seedAuditLog(
            logId = "STAT-AUD-$dateKey-B$branchId-TX-${orderNo.toString().padStart(3, '0')}",
            userId = userId,
            branchId = branchId,
            action = "Seeded completed transaction $transactionId",
            tableAffected = "transactions",
            timestamp = transactionTime
        )
    }

    private fun seedPeriodicRestocks(date: LocalDate) {
        val dayOfMonth = date.dayOfMonth

        if (dayOfMonth % 4 != 1) {
            return
        }

        val dateKey = dateKey(date)

        val restockIngredients = listOf(
            RestockSeed("Ice", 3023, 20000.0),
            RestockSeed("Evap", 3020, 5000.0),
            RestockSeed("Condense", 3021, 4000.0),
            RestockSeed("Sugar", 3022, 5000.0),
            RestockSeed("Mango", 3007, 30.0),
            RestockSeed("Apple", 3001, 25.0),
            RestockSeed("Banana", 3003, 30.0),
            RestockSeed("Pearl", 3015, 1000.0),
            RestockSeed("Medium Cups", 3024, 100.0),
            RestockSeed("Large Cups", 3025, 100.0),
            RestockSeed("Lids", 3026, 200.0),
            RestockSeed("Straws", 3027, 200.0)
        )

        listOf(1, 2).forEach { branchId ->
            val userId = if (branchId == 1) 2 else 3
            val selected = restockIngredients.shuffled(random).take(5)

            selected.forEachIndexed { index, item ->
                val restockId = "STAT-RS-$dateKey-B$branchId-${(index + 1).toString().padStart(3, '0')}"

                if (!restockLogRepository.existsById(restockId)) {
                    val time = epochMillis(date, 10 + index, random.nextInt(0, 60))

                    restockLogRepository.save(
                        RestockLogEntity(
                            restockId = restockId,
                            ingredientId = item.ingredientId,
                            branchId = branchId,
                            userId = userId,
                            quantityAdded = item.quantity,
                            supplier = "Seed Supplier",
                            dateTime = time,
                            lastModified = time,
                            isSynced = true,
                            syncedAt = time
                        )
                    )

                    seedAuditLog(
                        logId = "STAT-AUD-$dateKey-B$branchId-RS-${(index + 1).toString().padStart(3, '0')}",
                        userId = userId,
                        branchId = branchId,
                        action = "Seeded restock for ${item.name}",
                        tableAffected = "restock_logs",
                        timestamp = time
                    )
                }
            }
        }
    }

    private fun seedPeriodicWaste(date: LocalDate) {
        val dayOfMonth = date.dayOfMonth

        if (dayOfMonth % 3 != 0) {
            return
        }

        val dateKey = dateKey(date)

        val wasteIngredients = listOf(
            WasteSeed("Apple", 3001, 2.0),
            WasteSeed("Banana", 3003, 3.0),
            WasteSeed("Mango", 3007, 2.0),
            WasteSeed("Strawberry", 3009, 20.0),
            WasteSeed("Ice", 3023, 500.0),
            WasteSeed("Pearl", 3015, 100.0),
            WasteSeed("Medium Cups", 3024, 5.0),
            WasteSeed("Lids", 3026, 5.0)
        )

        val reasons = listOf(
            "Spoiled",
            "Preparation error",
            "Dropped item",
            "Expired",
            "Damaged packaging"
        )

        listOf(1, 2).forEach { branchId ->
            val userId = if (branchId == 1) 2 else 3
            val selected = wasteIngredients.shuffled(random).take(3)

            selected.forEachIndexed { index, item ->
                val wasteId = "STAT-WL-$dateKey-B$branchId-${(index + 1).toString().padStart(3, '0')}"

                if (!wasteLogRepository.existsById(wasteId)) {
                    val time = epochMillis(date, 15 + index, random.nextInt(0, 60))

                    wasteLogRepository.save(
                        WasteLogEntity(
                            wasteId = wasteId,
                            ingredientId = item.ingredientId,
                            branchId = branchId,
                            userId = userId,
                            quantity = item.quantity,
                            image = null,
                            reason = reasons.random(random),
                            dateTime = time,
                            lastModified = time,
                            isSynced = true,
                            syncedAt = time
                        )
                    )

                    seedAuditLog(
                        logId = "STAT-AUD-$dateKey-B$branchId-WL-${(index + 1).toString().padStart(3, '0')}",
                        userId = userId,
                        branchId = branchId,
                        action = "Seeded waste log for ${item.name}",
                        tableAffected = "waste_logs",
                        timestamp = time
                    )
                }
            }
        }
    }

    private fun seedAuditLog(
        logId: String,
        userId: Int,
        branchId: Int,
        action: String,
        tableAffected: String,
        timestamp: Long
    ) {
        if (!auditLogRepository.existsById(logId)) {
            auditLogRepository.save(
                AuditLogEntity(
                    logId = logId,
                    userId = userId,
                    branchId = branchId,
                    action = action,
                    tableAffected = tableAffected,
                    timestamp = timestamp,
                    lastModified = timestamp,
                    isSynced = true,
                    syncedAt = timestamp
                )
            )
        }
    }

    private fun pickRandomAddons(): List<MenuAddon> {
        val chosen = mutableListOf<MenuAddon>()

        if (random.nextDouble() < 0.35) {
            chosen.add(paidAddons.random(random))
        }

        if (random.nextDouble() < 0.25) {
            chosen.add(freeAddons.random(random))
        }

        return chosen.distinctBy { it.productId }
    }

    private fun epochMillis(
        date: LocalDate,
        hour: Int,
        minute: Int
    ): Long {
        return date
            .atTime(LocalTime.of(hour, minute))
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    private fun dateKey(date: LocalDate): String {
        return date.toString().replace("-", "")
    }

    private fun roundMoney(value: Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }

    private val menuProducts = listOf(
        MenuProduct(1001, 10011, 10012),
        MenuProduct(1002, 10021, 10022),
        MenuProduct(1003, 10031, 10032),
        MenuProduct(1004, 10041, 10042),
        MenuProduct(1005, 10051, 10052),
        MenuProduct(1006, 10061, 10062),
        MenuProduct(1007, 10071, 10072),
        MenuProduct(1008, 10081, 10082),
        MenuProduct(1009, 10091, 10092),
        MenuProduct(1010, 10101, 10102),
        MenuProduct(1011, 10111, 10112)
    )

    private val paidAddons = listOf(
        MenuAddon(2001, 10.0),
        MenuAddon(2002, 10.0),
        MenuAddon(2003, 10.0)
    )

    private val freeAddons = listOf(
        MenuAddon(2004, 0.0),
        MenuAddon(2005, 0.0),
        MenuAddon(2006, 0.0),
        MenuAddon(2007, 0.0),
        MenuAddon(2008, 0.0),
        MenuAddon(2009, 0.0)
    )

    private data class StaffSeed(
        val logId: String,
        val userId: Int,
        val branchId: Int,
        val clockIn: Long,
        val clockOut: Long
    )

    private data class MenuProduct(
        val productId: Int,
        val mediumVariantId: Int,
        val largeVariantId: Int
    )

    private data class MenuAddon(
        val productId: Int,
        val price: Double
    )

    private data class GeneratedTransactionItem(
        val transactionItemId: String,
        val productId: Int,
        val variantId: Int,
        val sizeName: String,
        val quantity: Int,
        val subtotal: Double,
        val addons: List<MenuAddon>
    )

    private data class RestockSeed(
        val name: String,
        val ingredientId: Int,
        val quantity: Double
    )

    private data class WasteSeed(
        val name: String,
        val ingredientId: Int,
        val quantity: Double
    )
}