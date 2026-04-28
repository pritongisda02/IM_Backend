package fruitylicious

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PosApplication

fun main(args: Array<String>) {
	runApplication<PosApplication>(*args)
}