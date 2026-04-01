package com.example.stocksheet

import com.example.stocksheet.scenarios.StandardMarketScenario
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(StandardMarketScenario::class)
abstract class BaseRepositoryTest : DescribeSpec() {
    @Autowired
    lateinit var entityManager: TestEntityManager

    companion object {
        @Container
        @ServiceConnection
        val postgres = PostgreSQLContainer("postgres:16-alpine")
    }
}
