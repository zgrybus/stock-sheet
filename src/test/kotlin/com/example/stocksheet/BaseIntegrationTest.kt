package com.example.stocksheet

import com.example.stocksheet.integration.fmp.dto.FmpCompanyProfileResponseDTO
import com.example.stocksheet.integration.fmp.service.FmpService
import com.example.stocksheet.mocks.TestDatabaseFactory
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
abstract class BaseIntegrationTest : DescribeSpec() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var fmpService: FmpService

    @Autowired
    lateinit var testDb: TestDatabaseFactory

    init {
        beforeEach {
            every { fmpService.getCompanyProfile(any<String>()) } answers {
                val symbol = firstArg<String>()

                FmpCompanyProfileResponseDTO(
                    name = "name".plus(symbol),
                    symbol = symbol,
                    exchange = "exchange".plus(symbol),
                    industry = "industry".plus(symbol),
                    lastDividend = BigDecimal.ZERO.setScale(4),
                    price = BigDecimal.ZERO.setScale(4),
                )
            }
        }
    }

    companion object {
        @Container
        @ServiceConnection
        val postgres = PostgreSQLContainer("postgres:16-alpine")
    }
}
