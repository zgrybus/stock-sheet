package com.example.stocksheet

import com.example.stocksheet.integration.finnhub.dto.FinnhubCompanyProfile2Response
import com.example.stocksheet.integration.finnhub.dto.FinnhubSymbolLookupResponse
import com.example.stocksheet.integration.finnhub.service.FinnhubService
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import tools.jackson.databind.ObjectMapper

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
abstract class BaseIntegrationTest : DescribeSpec() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var finnhubService: FinnhubService

    init {
        beforeEach {
            every { finnhubService.getSymbolLookup(any<String>(), any<String>()) } answers {
                FinnhubSymbolLookupResponse.FinnhubSymbolLookupType.ETP
            }

            every { finnhubService.getCompanyProfile2(any<String>()) } answers {
                val symbol = firstArg<String>()

                FinnhubCompanyProfile2Response(
                    name = "name".plus(symbol),
                    ticker = symbol,
                    exchange = "exchange".plus(symbol),
                    industry = "industry".plus(symbol),
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
