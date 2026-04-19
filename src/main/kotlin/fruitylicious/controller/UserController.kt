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
@RequestMapping("/api/user")
class UserController (
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

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): User {
        return userService.login(req.username, req.password)
    }

    data class LoginRequest(
        val username: String,
        val password: String
    )
}