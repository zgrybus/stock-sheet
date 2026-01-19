package com.example.stocksheet

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
abstract class BaseIntegrationTest(
    protected val mockMvc: MockMvc,
    protected val objectMapper: ObjectMapper,
) : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    companion object {
        @Container
        @ServiceConnection
        val postgres = PostgreSQLContainer("postgres:15-alpine")
    }
}
