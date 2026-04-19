package fruitylicious.service

import fruitylicious.entity.Product
import fruitylicious.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService (
    private val productRepository: ProductRepository
)
{
    fun createProduct(product: Product): Product{
        return productRepository.save(product)
    }

    fun getAllProduct():List<Product>{
        return productRepository.findByIsAddonFalse()
    }

    fun getAllAddOn(): List<Product>{
        return productRepository.findByIsAddonTrue()
    }

    fun updateProduct(id: Long, updated: Product): Product{
        val existing = productRepository.findById(id)
            .orElseThrow{ RuntimeException("Product does not exist")}

        existing.name = updated.name
        existing.price = updated.price
        existing.isAddon = updated.isAddon

        return productRepository.save(existing)
    }

    fun deleteProduct(id: Long){
        if (!productRepository.existsById(id)){
            throw RuntimeException("Product does not exist")
        }
        return productRepository.deleteById(id)
    }
}