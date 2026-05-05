package fruitylicious.config

import fruitylicious.entity.BranchEntity
import fruitylicious.entity.IngredientEntity
import fruitylicious.entity.InventoryEntity
import fruitylicious.entity.InventoryId
import fruitylicious.entity.ProductEntity
import fruitylicious.entity.ProductRecipeEntity
import fruitylicious.entity.ProductVariantEntity
import fruitylicious.repository.BranchRepository
import fruitylicious.repository.IngredientRepository
import fruitylicious.repository.InventoryRepository
import fruitylicious.repository.ProductRecipeRepository
import fruitylicious.repository.ProductRepository
import fruitylicious.repository.ProductVariantRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Order(1)
@ConditionalOnProperty(
    prefix = "fruitylicious.seed",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class BaseDataSeeder(
    private val branchRepository: BranchRepository,
    private val productRepository: ProductRepository,
    private val productVariantRepository: ProductVariantRepository,
    private val ingredientRepository: IngredientRepository,
    private val productRecipeRepository: ProductRecipeRepository,
    private val inventoryRepository: InventoryRepository,

    @Value("\${fruitylicious.seed.inventory-test-stock:true}")
    private val seedInventoryTestStock: Boolean,

    @Value("\${fruitylicious.seed.inventory-reset-existing:false}")
    private val resetExistingInventory: Boolean
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments) {
        val now = System.currentTimeMillis()

        seedBranches(now)
        seedProducts(now)
        seedProductVariants(now)
        seedIngredients(now)
        seedRecipes(now)
        seedInventoryRows(now)

        if (seedInventoryTestStock) {
            seedTestingInventoryStock(now)
        }
    }

    private fun seedBranches(now: Long) {
        val branches = listOf(
            BranchEntity(
                branchId = 1,
                branchName = "Branch 1",
                address = "Default Branch 1 Address",
                contactNumber = "N/A",
                lastModified = now,
                isSynced = true,
                syncedAt = now
            ),
            BranchEntity(
                branchId = 2,
                branchName = "Branch 2",
                address = "Default Branch 2 Address",
                contactNumber = "N/A",
                lastModified = now,
                isSynced = true,
                syncedAt = now
            )
        )

        branches.forEach { branch ->
            if (!branchRepository.existsById(branch.branchId)) {
                branchRepository.save(branch)
            }
        }
    }

    private fun seedProducts(now: Long) {
        val products = listOf(
            // Main shake products
            ProductSeed(1001, "Apple Shake", false, 0.0),
            ProductSeed(1002, "Avocado Shake", false, 0.0),
            ProductSeed(1003, "Banana Shake", false, 0.0),
            ProductSeed(1004, "Buko Shake", false, 0.0),
            ProductSeed(1005, "Dragon Fruit Shake", false, 0.0),
            ProductSeed(1006, "Guyabano Shake", false, 0.0),
            ProductSeed(1007, "Mango Shake", false, 0.0),
            ProductSeed(1008, "Melon Shake", false, 0.0),
            ProductSeed(1009, "Strawberry Shake", false, 0.0),
            ProductSeed(1010, "Oreo Shake", false, 0.0),
            ProductSeed(1011, "Cheesecake Shake", false, 0.0),

            // Paid add-ons
            ProductSeed(2001, "Cheese", true, 10.0),
            ProductSeed(2002, "Nata de Coco", true, 10.0),
            ProductSeed(2003, "Pearl", true, 10.0),

            // Free add-ons
            ProductSeed(2004, "Crushed Oreo", true, 0.0),
            ProductSeed(2005, "Crushed Graham", true, 0.0),
            ProductSeed(2006, "Syrup Caramel", true, 0.0),
            ProductSeed(2007, "Syrup Mango", true, 0.0),
            ProductSeed(2008, "Syrup Chocolate", true, 0.0),
            ProductSeed(2009, "Syrup Strawberry", true, 0.0)
        )

        products.forEach { seed ->
            if (!productRepository.existsById(seed.productId)) {
                productRepository.save(
                    ProductEntity(
                        productId = seed.productId,
                        image = null,
                        productName = seed.productName,
                        isAddon = seed.isAddon,
                        price = seed.price,
                        isDeleted = false,
                        deletedAt = null,
                        lastModified = now,
                        isSynced = true,
                        syncedAt = now
                    )
                )
            }
        }
    }

    private fun seedProductVariants(now: Long) {
        val mainProductIds = 1001..1011

        mainProductIds.forEach { productId ->
            val mediumVariantId = productId * 10 + 1
            val largeVariantId = productId * 10 + 2

            if (!productVariantRepository.existsById(mediumVariantId)) {
                productVariantRepository.save(
                    ProductVariantEntity(
                        variantId = mediumVariantId,
                        productId = productId,
                        sizeName = "Medium",
                        price = 60.0,
                        isDeleted = false,
                        deletedAt = null,
                        lastModified = now,
                        isSynced = true,
                        syncedAt = now
                    )
                )
            }

            if (!productVariantRepository.existsById(largeVariantId)) {
                productVariantRepository.save(
                    ProductVariantEntity(
                        variantId = largeVariantId,
                        productId = productId,
                        sizeName = "Large",
                        price = 80.0,
                        isDeleted = false,
                        deletedAt = null,
                        lastModified = now,
                        isSynced = true,
                        syncedAt = now
                    )
                )
            }
        }
    }

    private fun seedIngredients(now: Long) {
        val ingredients = listOf(
            IngredientSeed(3001, "Apple", "PCS", 150.0, 30.0, false),
            IngredientSeed(3002, "Avocado", "PCS", 250.0, 30.0, false),
            IngredientSeed(3003, "Banana", "PCS", 120.0, 30.0, false),
            IngredientSeed(3004, "Buko", "PCS", 300.0, 30.0, false),
            IngredientSeed(3005, "Dragon Fruit", "PCS", 400.0, 30.0, false),
            IngredientSeed(3006, "Guyabano", "PCS", 1000.0, 20.0, false),
            IngredientSeed(3007, "Mango", "PCS", 200.0, 30.0, false),
            IngredientSeed(3008, "Melon", "PCS", 1500.0, 20.0, false),
            IngredientSeed(3009, "Strawberry", "PCS", 15.0, 70.0, false),

            IngredientSeed(3010, "Oreo", "PCS", 11.0, 100.0, false),
            IngredientSeed(3011, "Crushed Graham", "G", 0.0, 500.0, false),
            IngredientSeed(3012, "Cheese", "G", 0.0, 500.0, false),
            IngredientSeed(3013, "Lemon Square Cheesecake", "PCS", 50.0, 30.0, false),
            IngredientSeed(3014, "Nata de Coco", "G", 0.0, 500.0, false),
            IngredientSeed(3015, "Pearl", "G", 0.0, 500.0, false),

            IngredientSeed(3016, "Syrup Caramel", "ML", 0.0, 500.0, false),
            IngredientSeed(3017, "Syrup Mango", "ML", 0.0, 500.0, false),
            IngredientSeed(3018, "Syrup Chocolate", "ML", 0.0, 500.0, false),
            IngredientSeed(3019, "Syrup Strawberry", "ML", 0.0, 500.0, false),

            IngredientSeed(3020, "Evap", "G", 0.0, 1000.0, false),
            IngredientSeed(3021, "Condense", "G", 0.0, 1000.0, false),
            IngredientSeed(3022, "Sugar", "G", 0.0, 2000.0, false),
            IngredientSeed(3023, "Ice", "G", 0.0, 5000.0, false),

            IngredientSeed(3024, "Medium Cups", "PCS", 0.0, 100.0, true),
            IngredientSeed(3025, "Large Cups", "PCS", 0.0, 100.0, true),
            IngredientSeed(3026, "Lids", "PCS", 0.0, 100.0, true),
            IngredientSeed(3027, "Straws", "PCS", 0.0, 200.0, true)
        )

        ingredients.forEach { seed ->
            if (!ingredientRepository.existsById(seed.ingredientId)) {
                ingredientRepository.save(
                    IngredientEntity(
                        ingredientId = seed.ingredientId,
                        image = null,
                        ingredientName = seed.ingredientName,
                        unitType = seed.unitType,
                        estimatedWeightPerUnit = seed.estimatedWeightPerUnit,
                        isPackaging = seed.isPackaging,
                        lowStockThreshold = seed.lowStockThreshold,
                        isDeleted = false,
                        deletedAt = null,
                        lastModified = now,
                        isSynced = true,
                        syncedAt = now
                    )
                )
            }
        }
    }

    private fun seedRecipes(now: Long) {
        val existingRecipes = productRecipeRepository.findAll()
        val recipeLines = buildRecipeLines()

        var nextRecipeId = 500001

        recipeLines.forEach { line ->
            val alreadyExists = existingRecipes.any {
                it.productId == line.productId &&
                        it.variantId == line.variantId &&
                        it.ingredientId == line.ingredientId
            }

            if (!alreadyExists) {
                while (productRecipeRepository.existsById(nextRecipeId)) {
                    nextRecipeId++
                }

                productRecipeRepository.save(
                    ProductRecipeEntity(
                        recipeId = nextRecipeId,
                        productId = line.productId,
                        variantId = line.variantId,
                        ingredientId = line.ingredientId,
                        quantityRequired = line.quantityRequired,
                        isDeleted = false,
                        deletedAt = null,
                        lastModified = now,
                        isSynced = true,
                        syncedAt = now
                    )
                )

                nextRecipeId++
            }
        }
    }

    private fun seedInventoryRows(now: Long) {
        val branchIds = listOf(1, 2)
        val ingredientIds = ingredientRepository.findAll()
            .map { it.ingredientId }

        branchIds.forEach { branchId ->
            ingredientIds.forEach { ingredientId ->
                val inventoryId = InventoryId(
                    ingredientId = ingredientId,
                    branchId = branchId
                )

                if (!inventoryRepository.existsById(inventoryId)) {
                    inventoryRepository.save(
                        InventoryEntity(
                            id = inventoryId,
                            currentStock = 0.0,
                            lastModified = now,
                            isSynced = true,
                            syncedAt = now
                        )
                    )
                }
            }
        }
    }

    private fun seedTestingInventoryStock(now: Long) {
        val branchIds = listOf(1, 2)

        val testStockByIngredientId = mapOf(
            // Fruits
            3001 to 80.0,
            3002 to 80.0,
            3003 to 120.0,
            3004 to 80.0,
            3005 to 60.0,
            3006 to 40.0,
            3007 to 100.0,
            3008 to 40.0,
            3009 to 800.0,

            // Special ingredients and add-ons
            3010 to 500.0,
            3011 to 3000.0,
            3012 to 3000.0,
            3013 to 100.0,
            3014 to 3000.0,
            3015 to 3000.0,

            // Syrups
            3016 to 3000.0,
            3017 to 3000.0,
            3018 to 3000.0,
            3019 to 3000.0,

            // Base ingredients
            3020 to 10000.0,
            3021 to 8000.0,
            3022 to 10000.0,
            3023 to 50000.0,

            // Packaging
            3024 to 300.0,
            3025 to 300.0,
            3026 to 600.0,
            3027 to 600.0
        )

        branchIds.forEach { branchId ->
            testStockByIngredientId.forEach { (ingredientId, stock) ->
                val inventoryId = InventoryId(
                    ingredientId = ingredientId,
                    branchId = branchId
                )

                val existingInventory = inventoryRepository.findById(inventoryId).orElse(null)

                if (existingInventory == null) {
                    inventoryRepository.save(
                        InventoryEntity(
                            id = inventoryId,
                            currentStock = stock,
                            lastModified = now,
                            isSynced = true,
                            syncedAt = now
                        )
                    )
                } else if (resetExistingInventory || existingInventory.currentStock == 0.0) {
                    existingInventory.currentStock = stock
                    existingInventory.lastModified = now
                    existingInventory.isSynced = true
                    existingInventory.syncedAt = now

                    inventoryRepository.save(existingInventory)
                }
            }
        }
    }

    private fun buildRecipeLines(): List<RecipeSeed> {
        val productMainIngredients = listOf(
            ProductMainIngredient(1001, 3001, 150.0, 220.0), // Apple Shake
            ProductMainIngredient(1002, 3002, 150.0, 220.0), // Avocado Shake
            ProductMainIngredient(1003, 3003, 150.0, 220.0), // Banana Shake
            ProductMainIngredient(1004, 3004, 150.0, 220.0), // Buko Shake
            ProductMainIngredient(1005, 3005, 150.0, 220.0), // Dragon Fruit Shake
            ProductMainIngredient(1006, 3006, 150.0, 220.0), // Guyabano Shake
            ProductMainIngredient(1007, 3007, 150.0, 220.0), // Mango Shake
            ProductMainIngredient(1008, 3008, 150.0, 220.0), // Melon Shake
            ProductMainIngredient(1009, 3009, 150.0, 220.0), // Strawberry Shake
            ProductMainIngredient(1010, 3010, 3.0, 5.0),     // Oreo Shake
            ProductMainIngredient(1011, 3013, 1.0, 2.0)      // Cheesecake Shake
        )

        val recipeLines = mutableListOf<RecipeSeed>()

        productMainIngredients.forEach { item ->
            val mediumVariantId = item.productId * 10 + 1
            val largeVariantId = item.productId * 10 + 2

            // Medium recipe
            recipeLines.add(RecipeSeed(item.productId, mediumVariantId, 3023, 150.0)) // Ice
            recipeLines.add(RecipeSeed(item.productId, mediumVariantId, 3020, 80.0))  // Evap
            recipeLines.add(RecipeSeed(item.productId, mediumVariantId, 3021, 30.0))  // Condense
            recipeLines.add(RecipeSeed(item.productId, mediumVariantId, 3022, 10.0))  // Sugar
            recipeLines.add(RecipeSeed(item.productId, mediumVariantId, item.ingredientId, item.mediumQty))
            recipeLines.add(RecipeSeed(item.productId, mediumVariantId, 3024, 1.0))   // Medium Cups
            recipeLines.add(RecipeSeed(item.productId, mediumVariantId, 3026, 1.0))   // Lids
            recipeLines.add(RecipeSeed(item.productId, mediumVariantId, 3027, 1.0))   // Straws

            // Large recipe
            recipeLines.add(RecipeSeed(item.productId, largeVariantId, 3023, 220.0)) // Ice
            recipeLines.add(RecipeSeed(item.productId, largeVariantId, 3020, 110.0)) // Evap
            recipeLines.add(RecipeSeed(item.productId, largeVariantId, 3021, 40.0))  // Condense
            recipeLines.add(RecipeSeed(item.productId, largeVariantId, 3022, 15.0))  // Sugar
            recipeLines.add(RecipeSeed(item.productId, largeVariantId, item.ingredientId, item.largeQty))
            recipeLines.add(RecipeSeed(item.productId, largeVariantId, 3025, 1.0))   // Large Cups
            recipeLines.add(RecipeSeed(item.productId, largeVariantId, 3026, 1.0))   // Lids
            recipeLines.add(RecipeSeed(item.productId, largeVariantId, 3027, 1.0))   // Straws
        }

        // Paid add-ons
        recipeLines.add(RecipeSeed(2001, null, 3012, 20.0)) // Cheese
        recipeLines.add(RecipeSeed(2002, null, 3014, 30.0)) // Nata de Coco
        recipeLines.add(RecipeSeed(2003, null, 3015, 30.0)) // Pearl

        // Free add-ons
        recipeLines.add(RecipeSeed(2004, null, 3010, 10.0)) // Crushed Oreo uses Oreo
        recipeLines.add(RecipeSeed(2005, null, 3011, 10.0)) // Crushed Graham

        recipeLines.add(RecipeSeed(2006, null, 3016, 10.0)) // Syrup Caramel
        recipeLines.add(RecipeSeed(2007, null, 3017, 10.0)) // Syrup Mango
        recipeLines.add(RecipeSeed(2008, null, 3018, 10.0)) // Syrup Chocolate
        recipeLines.add(RecipeSeed(2009, null, 3019, 10.0)) // Syrup Strawberry

        return recipeLines
    }

    private data class ProductSeed(
        val productId: Int,
        val productName: String,
        val isAddon: Boolean,
        val price: Double
    )

    private data class IngredientSeed(
        val ingredientId: Int,
        val ingredientName: String,
        val unitType: String,
        val estimatedWeightPerUnit: Double,
        val lowStockThreshold: Double,
        val isPackaging: Boolean
    )

    private data class ProductMainIngredient(
        val productId: Int,
        val ingredientId: Int,
        val mediumQty: Double,
        val largeQty: Double
    )

    private data class RecipeSeed(
        val productId: Int,
        val variantId: Int?,
        val ingredientId: Int,
        val quantityRequired: Double
    )
}