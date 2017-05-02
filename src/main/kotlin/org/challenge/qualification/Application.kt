package org.challenge.qualification

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan
open class ApplicationConfig

fun main(args: Array<String>) {
    SpringApplication.run(ApplicationConfig::class.java)
}