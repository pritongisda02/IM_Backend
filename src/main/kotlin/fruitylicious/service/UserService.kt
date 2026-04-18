package fruitylicious.service
import fruitylicious.Config
import fruitylicious.entity.User
import fruitylicious.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
)
{
    fun createUser(user: User): User{
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    fun getAllUser(): List<User>{
        return userRepository.findAll()
    }

    fun updateUser(id: Long, updated: User): User{
        val existing = userRepository.findById(id)
            .orElseThrow{ RuntimeException("User not found")}
        existing.name = updated.name
        existing.username = updated.username
        existing.role = updated.role
        existing.password = passwordEncoder.encode(updated.password)

        return userRepository.save(existing)
    }

    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw RuntimeException("User not found")
        }
        userRepository.deleteById(id)
    }

}

