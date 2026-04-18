package fruitylicious.controller

import fruitylicious.entity.Ingredient
import fruitylicious.service.IngredientService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ingredient")
class IngredientController (
    private val ingredientService: IngredientService
)
{
    @GetMapping
    fun getAllIngredient(): List<Ingredient>{
        return ingredientService.getAllIngredient()
    }

    @PostMapping
    fun createIngredient(@RequestBody ingredient: Ingredient): Ingredient{
        return ingredientService.createIngredient(ingredient)
    }

    @PutMapping("/{id}")
    fun updateIngredient(@PathVariable id: Long, @RequestBody updated: Ingredient): Ingredient{
        return ingredientService.updateIngredient(id, updated)
    }

    @DeleteMapping("/{id}")
    fun deleteIngredient(@PathVariable id: Long){
        return ingredientService.deleteIngredient(id)
    }
}