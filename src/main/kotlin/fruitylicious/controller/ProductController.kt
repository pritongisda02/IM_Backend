package fruitylicious.controller

import fruitylicious.entity.User
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
    private val userService: UserService
)
{
    @GetMapping
    fun getAllUser(): List<User>{
        return userService.getAllUser()
    }

    @PostMapping
    fun createUser(@RequestBody user: User): User {
        return userService.createUser(user)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody updated: User): User{
        return userService.updateUser(id, updated)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long) {
        userService.deleteUser(id)
    }
}