package fruitylicious.controller

import fruitylicious.entity.Product
import fruitylicious.entity.User
import fruitylicious.service.ProductService
import fruitylicious.service.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/product")
class ProductController (
    private val productService: ProductService
)
{
    @GetMapping
    fun getAllProduct(): List<Product>{
        return productService.getAllProduct()
    }

    @GetMapping("/addOn")
    fun getAllAddOn(): List<Product>{
        return productService.getAllAddOn()
    }

    @PostMapping
    fun createProduct(@RequestBody product: Product): Product {
        return productService.createProduct(product)
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody updated: Product): Product{
        return productService.updateProduct(id, updated)
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long) {
        productService.deleteProduct(id)
    }
}