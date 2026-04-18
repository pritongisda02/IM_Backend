package fruitylicious

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PosSystemApplication

fun main(args: Array<String>) {
	runApplication<PosSystemApplication>(*args)
}
