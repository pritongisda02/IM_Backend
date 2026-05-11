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
import java.time.DayOfWeek
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

    @Value("\${fruitylicious.seed.stats.start-date:}")
    private val startDateText: String,

    @Value("\${fruitylicious.seed.stats.end-date:}")
    private val endDateText: String
) : ApplicationRunner {

    private val zoneId: ZoneId = ZoneId.of("Asia/Manila")
    private val random = Random(20260301)

    private val salesStartMinute = 10 * 60
    private val salesEndMinute = 20 * 60

    @Transactional
    override fun run(args: ApplicationArguments) {
        seedDefaultUsersIfMissing()

        val today = LocalDate.now(zoneId)

        val startDate = if (startDateText.isBlank()) {
            today.minusDays(6)
        } else {
            LocalDate.parse(startDateText)
        }

        val endDate = if (endDateText.isBlank()) {
            today
        } else {
            LocalDate.parse(endDateText).coerceAtMost(today)
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
                name = "Main Admin",
                role = "admin",
                username = "admin",
                password = "admin1234",
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
                password = "staff1234",
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
                password = "staff1234",
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
                clockIn = epochMillis(date, 9, 30),
                clockOut = epochMillis(date, 20, 30)
            ),
            StaffSeed(
                logId = "STAT-SL-$dateKey-B2-U3",
                userId = 3,
                branchId = 2,
                clockIn = epochMillis(date, 9, 45),
                clockOut = epochMillis(date, 20, 15)
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
            val orderCount = realisticOrderCount(date, branchId)

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

    private fun realisticOrderCount(
        date: LocalDate,
        branchId: Int
    ): Int {
        val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY ||
                date.dayOfWeek == DayOfWeek.SUNDAY

        val today = LocalDate.now(zoneId)
        val isToday = date == today
        val currentMinute = LocalTime.now(zoneId).let { it.hour * 60 + it.minute }

        val fullDayBase = if (isWeekend) {
            if (branchId == 1) random.nextInt(22, 36) else random.nextInt(18, 30)
        } else {
            if (branchId == 1) random.nextInt(14, 26) else random.nextInt(10, 22)
        }

        if (!isToday) {
            return fullDayBase
        }

        val elapsedSalesMinutes = (currentMinute - salesStartMinute)
            .coerceAtLeast(0)
            .coerceAtMost(salesEndMinute - salesStartMinute)

        val salesWindowMinutes = salesEndMinute - salesStartMinute

        val ratio = if (salesWindowMinutes <= 0) {
            1.0
        } else {
            elapsedSalesMinutes.toDouble() / salesWindowMinutes.toDouble()
        }

        return (fullDayBase * ratio)
            .roundToInt()
            .coerceAtLeast(if (currentMinute >= salesStartMinute) 1 else 0)
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

        val itemCount = weightedItemCount()
        val selectedItems = mutableListOf<GeneratedTransactionItem>()
        var transactionTotal = 0.0

        repeat(itemCount) { index ->
            val menuItem = pickWeightedProduct()
            val isLarge = random.nextDouble() < 0.38
            val variantId = if (isLarge) {
                menuItem.largeVariantId
            } else {
                menuItem.mediumVariantId
            }

            val variant = productVariantRepository.findById(variantId).orElse(null)
                ?: return@repeat

            val quantity = weightedQuantity()
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

        val transactionTime = randomTransactionTime(date)
        val paymentType = pickPaymentType()

        val transactionName = generateTransactionName(
            branchId = branchId,
            orderNo = orderNo,
            paymentType = paymentType
        )

        transactionRepository.save(
            TransactionEntity(
                transactionId = transactionId,
                userId = userId,
                branchId = branchId,
                transactionName = transactionName,
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
            RestockSeed("Ice", 3023, 25000.0),
            RestockSeed("Evap", 3020, 6000.0),
            RestockSeed("Condense", 3021, 5000.0),
            RestockSeed("Sugar", 3022, 7000.0),

            // Fruits are now in grams.
            RestockSeed("Mango", 3007, 12000.0),
            RestockSeed("Apple", 3001, 9000.0),
            RestockSeed("Banana", 3003, 9000.0),
            RestockSeed("Avocado", 3002, 10000.0),
            RestockSeed("Strawberry", 3009, 6000.0),
            RestockSeed("Melon", 3008, 15000.0),
            RestockSeed("Buko", 3004, 12000.0),

            RestockSeed("Pearl", 3015, 2500.0),
            RestockSeed("Cheese", 3012, 2500.0),
            RestockSeed("Nata de Coco", 3014, 2500.0),
            RestockSeed("Crushed Graham", 3011, 2000.0),

            RestockSeed("Syrup Caramel", 3016, 2000.0),
            RestockSeed("Syrup Chocolate", 3018, 2000.0),
            RestockSeed("Syrup Strawberry", 3019, 2000.0),

            RestockSeed("Oreo", 3010, 120.0),
            RestockSeed("Lemon Square Cheesecake", 3013, 40.0),

            RestockSeed("Medium Cups", 3024, 150.0),
            RestockSeed("Large Cups", 3025, 120.0),
            RestockSeed("Lids", 3026, 250.0),
            RestockSeed("Straws", 3027, 250.0)
        )

        listOf(1, 2).forEach { branchId ->
            val userId = if (branchId == 1) 2 else 3
            val selected = restockIngredients.shuffled(random).take(random.nextInt(6, 10))

            selected.forEachIndexed { index, item ->
                val restockId = "STAT-RS-$dateKey-B$branchId-${(index + 1).toString().padStart(3, '0')}"

                if (!restockLogRepository.existsById(restockId)) {
                    val time = randomOperationalTime(
                        date = date,
                        startHour = 9,
                        startMinute = 30,
                        endHour = 12,
                        endMinute = 30
                    )

                    restockLogRepository.save(
                        RestockLogEntity(
                            restockId = restockId,
                            ingredientId = item.ingredientId,
                            branchId = branchId,
                            userId = userId,
                            quantityAdded = item.quantity,
                            supplier = pickSupplier(),
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
            WasteSeed("Apple", 3001, 300.0),
            WasteSeed("Banana", 3003, 360.0),
            WasteSeed("Mango", 3007, 400.0),
            WasteSeed("Strawberry", 3009, 150.0),
            WasteSeed("Avocado", 3002, 500.0),
            WasteSeed("Melon", 3008, 800.0),
            WasteSeed("Ice", 3023, 1000.0),
            WasteSeed("Pearl", 3015, 250.0),
            WasteSeed("Cheese", 3012, 150.0),
            WasteSeed("Nata de Coco", 3014, 200.0),
            WasteSeed("Syrup Chocolate", 3018, 100.0),
            WasteSeed("Medium Cups", 3024, 5.0),
            WasteSeed("Large Cups", 3025, 3.0),
            WasteSeed("Lids", 3026, 6.0),
            WasteSeed("Straws", 3027, 6.0)
        )

        val reasons = listOf(
            "Spoiled",
            "Expired",
            "Preparation error",
            "Dropped item",
            "Damaged packaging",
            "Over-prepared",
            "Contaminated during preparation"
        )

        listOf(1, 2).forEach { branchId ->
            val userId = if (branchId == 1) 2 else 3
            val selected = wasteIngredients.shuffled(random).take(random.nextInt(2, 5))

            selected.forEachIndexed { index, item ->
                val wasteId = "STAT-WL-$dateKey-B$branchId-${(index + 1).toString().padStart(3, '0')}"

                if (!wasteLogRepository.existsById(wasteId)) {
                    val time = randomOperationalTime(
                        date = date,
                        startHour = 14,
                        startMinute = 0,
                        endHour = 20,
                        endMinute = 30
                    )

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

    private fun weightedItemCount(): Int {
        val roll = random.nextDouble()

        return when {
            roll < 0.72 -> 1
            roll < 0.93 -> 2
            else -> 3
        }
    }

    private fun weightedQuantity(): Int {
        val roll = random.nextDouble()

        return when {
            roll < 0.78 -> 1
            roll < 0.95 -> 2
            else -> 3
        }
    }

    private fun pickWeightedProduct(): MenuProduct {
        val totalWeight = menuProducts.sumOf { it.weight }
        var pick = random.nextInt(totalWeight)

        for (item in menuProducts) {
            pick -= item.weight
            if (pick < 0) {
                return item
            }
        }

        return menuProducts.first()
    }

    private fun pickRandomAddons(): List<MenuAddon> {
        val chosen = mutableListOf<MenuAddon>()

        if (random.nextDouble() < 0.42) {
            chosen.add(paidAddons.random(random))
        }

        if (random.nextDouble() < 0.22) {
            chosen.add(freeAddons.random(random))
        }

        if (random.nextDouble() < 0.08) {
            chosen.add(freeAddons.random(random))
        }

        return chosen.distinctBy { it.productId }
    }

    private fun pickPaymentType(): String {
        return if (random.nextDouble() < 0.58) {
            "Cash"
        } else {
            "Gcash"
        }
    }

    private fun pickSupplier(): String {
        val suppliers = listOf(
            "Local Fruit Supplier",
            "Fresh Market Supplier",
            "Packaging Supplier",
            "Dairy and Dry Goods Supplier",
            "Branch Stock Delivery"
        )

        return suppliers.random(random)
    }

    private fun randomTransactionTime(date: LocalDate): Long {
        val today = LocalDate.now(zoneId)

        val maxMinuteOfDay = if (date == today) {
            val now = LocalTime.now(zoneId)
            val currentMinute = now.hour * 60 + now.minute

            currentMinute
                .coerceAtMost(salesEndMinute)
                .coerceAtLeast(salesStartMinute)
        } else {
            salesEndMinute
        }

        val selectedMinuteOfDay = if (maxMinuteOfDay <= salesStartMinute) {
            salesStartMinute
        } else {
            weightedSalesMinute(
                minMinute = salesStartMinute,
                maxMinute = maxMinuteOfDay
            )
        }

        val hour = selectedMinuteOfDay / 60
        val minute = selectedMinuteOfDay % 60

        return epochMillis(date, hour, minute)
    }

    private fun weightedSalesMinute(
        minMinute: Int,
        maxMinute: Int
    ): Int {
        val salesMinute = when {
            random.nextDouble() < 0.18 -> random.nextInt(10 * 60, 12 * 60)
            random.nextDouble() < 0.45 -> random.nextInt(12 * 60, 15 * 60)
            random.nextDouble() < 0.78 -> random.nextInt(15 * 60, 18 * 60)
            else -> random.nextInt(18 * 60, 20 * 60 + 1)
        }

        return salesMinute.coerceIn(minMinute, maxMinute)
    }

    private fun randomOperationalTime(
        date: LocalDate,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ): Long {
        val today = LocalDate.now(zoneId)
        val start = startHour * 60 + startMinute
        val end = endHour * 60 + endMinute

        val maxMinuteOfDay = if (date == today) {
            val now = LocalTime.now(zoneId)
            val currentMinute = now.hour * 60 + now.minute

            currentMinute
                .coerceAtMost(end)
                .coerceAtLeast(start)
        } else {
            end
        }

        val selectedMinuteOfDay = if (maxMinuteOfDay <= start) {
            start
        } else {
            random.nextInt(start, maxMinuteOfDay + 1)
        }

        val hour = selectedMinuteOfDay / 60
        val minute = selectedMinuteOfDay % 60

        return epochMillis(date, hour, minute)
    }

    private fun generateTransactionName(
        branchId: Int,
        orderNo: Int,
        paymentType: String
    ): String {
        val names = if (paymentType.equals("Gcash", ignoreCase = true)) {
            listOf(
                "GCash Customer",
                "Mobile Payment Order",
                "Online Payment Pickup",
                "QR Payment Customer"
            )
        } else {
            listOf(
                "Walk-in Customer",
                "Takeout Order",
                "Dine-in Customer",
                "Regular Customer",
                "Student Customer",
                "Office Customer",
                "Family Order",
                "Group Order",
                "Quick Pickup"
            )
        }

        val baseName = names.random(random)

        return "$baseName B$branchId-${orderNo.toString().padStart(3, '0')}"
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
        MenuProduct(1001, 10011, 10012, 13), // Apple
        MenuProduct(1002, 10021, 10022, 11), // Avocado
        MenuProduct(1003, 10031, 10032, 10), // Banana
        MenuProduct(1004, 10041, 10042, 7),  // Buko
        MenuProduct(1005, 10051, 10052, 5),  // Dragon Fruit
        MenuProduct(1006, 10061, 10062, 5),  // Guyabano
        MenuProduct(1007, 10071, 10072, 18), // Mango
        MenuProduct(1008, 10081, 10082, 8),  // Melon
        MenuProduct(1009, 10091, 10092, 12), // Strawberry
        MenuProduct(1010, 10101, 10102, 15), // Oreo
        MenuProduct(1011, 10111, 10112, 9)   // Cheesecake
    )

    private val paidAddons = listOf(
        MenuAddon(2001, 10.0), // Cheese
        MenuAddon(2002, 10.0), // Nata de Coco
        MenuAddon(2003, 10.0)  // Pearl
    )

    private val freeAddons = listOf(
        MenuAddon(2004, 0.0), // Crushed Oreo
        MenuAddon(2005, 0.0), // Crushed Graham
        MenuAddon(2006, 0.0), // Syrup Caramel
        MenuAddon(2007, 0.0), // Syrup Mango
        MenuAddon(2008, 0.0), // Syrup Chocolate
        MenuAddon(2009, 0.0)  // Syrup Strawberry
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
        val largeVariantId: Int,
        val weight: Int
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