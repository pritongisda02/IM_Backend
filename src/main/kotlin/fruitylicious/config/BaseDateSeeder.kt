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
            ProductSeed(1001, "Apple", false, 0.0),
            ProductSeed(1002, "Avocado", false, 0.0),
            ProductSeed(1003, "Banana", false, 0.0),
            ProductSeed(1004, "Buko", false, 0.0),
            ProductSeed(1005, "Dragon Fruit", false, 0.0),
            ProductSeed(1006, "Guyabano", false, 0.0),
            ProductSeed(1007, "Mango", false, 0.0),
            ProductSeed(1008, "Melon", false, 0.0),
            ProductSeed(1009, "Strawberry", false, 0.0),
            ProductSeed(1010, "Oreo", false, 0.0),
            ProductSeed(1011, "Cheesecake", false, 0.0),

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
            // Fruits are now stocked directly in grams.
            IngredientSeed(3001, "Apple", "grams", 4500.0, false),
            IngredientSeed(3002, "Avocado", "grams", 7500.0, false),
            IngredientSeed(3003, "Banana", "grams", 3600.0, false),
            IngredientSeed(3004, "Buko", "grams", 9000.0, false),
            IngredientSeed(3005, "Dragon Fruit", "grams", 12000.0, false),
            IngredientSeed(3006, "Guyabano", "grams", 20000.0, false),
            IngredientSeed(3007, "Mango", "grams", 6000.0, false),
            IngredientSeed(3008, "Melon", "grams", 30000.0, false),
            IngredientSeed(3009, "Strawberry", "grams", 1050.0, false),

            // Count-based special ingredients
            IngredientSeed(3010, "Oreo", "pcs", 100.0, false),
            IngredientSeed(3013, "Lemon Square Cheesecake", "pcs", 30.0, false),

            // Gram-based add-ons and dry ingredients
            IngredientSeed(3011, "Crushed Graham", "grams", 500.0, false),
            IngredientSeed(3012, "Cheese", "grams", 500.0, false),
            IngredientSeed(3014, "Nata de Coco", "grams", 500.0, false),
            IngredientSeed(3015, "Pearl", "grams", 500.0, false),

            // ML-based syrups
            IngredientSeed(3016, "Syrup Caramel", "ml", 500.0, false),
            IngredientSeed(3017, "Syrup Mango", "ml", 500.0, false),
            IngredientSeed(3018, "Syrup Chocolate", "ml", 500.0, false),
            IngredientSeed(3019, "Syrup Strawberry", "ml", 500.0, false),

            // Base ingredients
            IngredientSeed(3020, "Evap", "grams", 1000.0, false),
            IngredientSeed(3021, "Condense", "grams", 1000.0, false),
            IngredientSeed(3022, "Sugar", "grams", 2000.0, false),
            IngredientSeed(3023, "Ice", "grams", 5000.0, false),

            // Packaging
            IngredientSeed(3024, "Medium Cups", "pcs", 100.0, true),
            IngredientSeed(3025, "Large Cups", "pcs", 100.0, true),
            IngredientSeed(3026, "Lids", "pcs", 100.0, true),
            IngredientSeed(3027, "Straws", "pcs", 200.0, true)
        )

        ingredients.forEach { seed ->
            if (!ingredientRepository.existsById(seed.ingredientId)) {
                ingredientRepository.save(
                    IngredientEntity(
                        ingredientId = seed.ingredientId,
                        image = null,
                        ingredientName = seed.ingredientName,
                        unitType = seed.unitType,
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
            // Fruits are stocked in grams.
            3001 to 12000.0,  // Apple
            3002 to 20000.0,  // Avocado
            3003 to 14400.0,  // Banana
            3004 to 24000.0,  // Buko
            3005 to 24000.0,  // Dragon Fruit
            3006 to 40000.0,  // Guyabano
            3007 to 20000.0,  // Mango
            3008 to 60000.0,  // Melon
            3009 to 12000.0,  // Strawberry

            // Count-based special ingredients
            3010 to 500.0,    // Oreo pcs
            3013 to 100.0,    // Lemon Square Cheesecake pcs

            // Gram-based add-ons/dry ingredients
            3011 to 3000.0,   // Crushed Graham
            3012 to 3000.0,   // Cheese
            3014 to 3000.0,   // Nata de Coco
            3015 to 3000.0,   // Pearl

            // Syrups in ml
            3016 to 3000.0,   // Syrup Caramel
            3017 to 3000.0,   // Syrup Mango
            3018 to 3000.0,   // Syrup Chocolate
            3019 to 3000.0,   // Syrup Strawberry

            // Base ingredients in grams
            3020 to 10000.0,  // Evap
            3021 to 8000.0,   // Condense
            3022 to 10000.0,  // Sugar
            3023 to 50000.0,  // Ice

            // Packaging in pcs
            3024 to 300.0,    // Medium Cups
            3025 to 300.0,    // Large Cups
            3026 to 600.0,    // Lids
            3027 to 600.0     // Straws
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
            ProductMainIngredient(1001, 3001, 150.0, 220.0), // Apple
            ProductMainIngredient(1002, 3002, 150.0, 220.0), // Avocado
            ProductMainIngredient(1003, 3003, 150.0, 220.0), // Banana
            ProductMainIngredient(1004, 3004, 150.0, 220.0), // Buko
            ProductMainIngredient(1005, 3005, 150.0, 220.0), // Dragon Fruit
            ProductMainIngredient(1006, 3006, 150.0, 220.0), // Guyabano
            ProductMainIngredient(1007, 3007, 150.0, 220.0), // Mango
            ProductMainIngredient(1008, 3008, 150.0, 220.0), // Melon
            ProductMainIngredient(1009, 3009, 150.0, 220.0), // Strawberry
            ProductMainIngredient(1010, 3010, 3.0, 5.0),     // Oreo pcs
            ProductMainIngredient(1011, 3013, 1.0, 2.0)      // Cheesecake pcs
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