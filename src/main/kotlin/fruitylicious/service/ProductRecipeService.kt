package fruitylicious.service

import fruitylicious.repository.oracle.ProductRecipeRepository
import org.springframework.stereotype.Service

@Service
class ProductRecipeService (
    private val recipeRepository: ProductRecipeRepository
)
{
    fun createRecipe(){

    }
}