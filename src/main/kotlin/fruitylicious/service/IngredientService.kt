package fruitylicious.service

import fruitylicious.entity.Ingredient
import fruitylicious.repository.IngredientRepository
import org.springframework.stereotype.Service

@Service
class IngredientService (
    private val ingredientRepository: IngredientRepository
)
{
    fun createIngredient(ingredient: Ingredient): Ingredient{
        return ingredientRepository.save(ingredient)
    }

    fun getAllIngredient(): List<Ingredient>{
        return ingredientRepository.findAll()
    }

    fun updateIngredient(id: Long, updated: Ingredient): Ingredient{
        val existing = ingredientRepository.findById(id)
            .orElseThrow{ RuntimeException("Ingredient does not exist")}

        existing.name = updated.name
        existing.unitType = updated.unitType
        existing.estimatedWeightPerUnit = updated.estimatedWeightPerUnit
        existing.isPackaging = existing.isPackaging

        return ingredientRepository.save(existing)
    }

    fun deleteIngredient(id: Long){
        if (!ingredientRepository.existsById(id)){
            throw RuntimeException("Ingredient does not exist")
        }
        ingredientRepository.deleteById(id)
    }

    fun searchIngredient(name: String): List<Ingredient>{
        return ingredientRepository.findByNameContainingIgnoreCase(name)
    }
}