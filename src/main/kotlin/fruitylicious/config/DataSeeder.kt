package fruitylicious.config

import fruitylicious.entity.BranchEntity
import fruitylicious.entity.UserEntity
import fruitylicious.repository.BranchRepository
import fruitylicious.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class DataSeeder {

    @Bean
    fun seedData(
        branchRepository: BranchRepository,
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder
    ): CommandLineRunner {
        return CommandLineRunner {
            val now = System.currentTimeMillis()

            if (!branchRepository.existsById(1)) {
                branchRepository.save(
                    BranchEntity(
                        branchId = 1,
                        branchName = "Branch 1",
                        address = "Default Branch 1 Address",
                        contactNumber = "N/A",
                        lastModified = now
                    )
                )
            }

            if (!branchRepository.existsById(2)) {
                branchRepository.save(
                    BranchEntity(
                        branchId = 2,
                        branchName = "Branch 2",
                        address = "Default Branch 2 Address",
                        contactNumber = "N/A",
                        lastModified = now
                    )
                )
            }

            if (userRepository.findByUsername("admin") == null) {
                userRepository.save(
                    UserEntity(
                        userId = 1,
                        name = "Default Admin",
                        role = "admin",
                        username = "admin",
                        password = passwordEncoder.encode("admin123"),
                        lastModified = now
                    )
                )
            }

            if (userRepository.findByUsername("staff") == null) {
                userRepository.save(
                    UserEntity(
                        userId = 2,
                        name = "Default Staff",
                        role = "staff",
                        username = "staff",
                        password = passwordEncoder.encode("staff123"),
                        lastModified = now
                    )
                )
            }
        }
    }
}