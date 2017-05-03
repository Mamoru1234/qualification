package org.challenge.qualification

import org.challenge.qualification.dao.TestRepo
import org.challenge.qualification.entity.TestEntity
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
@ComponentScan
open class ApplicationConfig {
    @Bean
    open fun init(repository: TestRepo) = CommandLineRunner {
        repository.save(TestEntity("My name"))
        repository.save(TestEntity("My name 123"))
        repository.save(TestEntity("My name 1234"))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(ApplicationConfig::class.java)
}