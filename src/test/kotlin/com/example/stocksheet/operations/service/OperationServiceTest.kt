package com.example.stocksheet.operations.service

import com.example.stocksheet.operations.dto.OperationRequestDTO
import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.dto.StockPositionDTO
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.mapper.toEntity
import com.example.stocksheet.operations.repository.OperationRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

class OperationServiceTest : DescribeSpec() {
    private var operationRepositoryMock: OperationRepository = mockk()
    private val operationService = OperationService(operationRepositoryMock)

    init {
        fun getOperationEntity(): OperationEntity =
            OperationEntity(
                id = 100,
                externalId = "external-id-1",
                stockSymbol = "GOOG.US",
                type = OperationType.BUY,
                volume = 10.toBigDecimal(),
                openDate = Instant.now(),
                pricePerVolume = 150.toBigDecimal(),
                totalPrice = 1500.toBigDecimal(),
                currency = "USD",
            )

        val operations =
            listOf(
                getOperationEntity(),
                getOperationEntity().apply {
                    id = 101
                    externalId = "external-id-2"
                    volume = 15.toBigDecimal()
                    pricePerVolume = 95.toBigDecimal()
                    totalPrice = 1425.toBigDecimal()
                },
                getOperationEntity().apply {
                    id = 102
                    externalId = "external-id-3"
                    volume = 2.toBigDecimal()
                    pricePerVolume = 500.toBigDecimal()
                    totalPrice = 1000.toBigDecimal()
                    stockSymbol = "TSLA.US"
                },
                getOperationEntity().apply {
                    id = 103
                    externalId = "external-id-4"
                    volume = 95.toBigDecimal()
                    pricePerVolume = 2.toBigDecimal()
                    totalPrice = 190.toBigDecimal()
                    stockSymbol = "ALPH.US"
                },
                getOperationEntity().apply {
                    id = 103
                    externalId = "external-id-4"
                    volume = 10.toBigDecimal()
                    pricePerVolume = 5.toBigDecimal()
                    totalPrice = 50.toBigDecimal()
                    stockSymbol = "TSLA.US"
                },
            )

        val currency = "USD"

        describe("OperationServiceTest") {
            describe("getPortfolioSummary") {
                beforeEach {
                    every { operationRepositoryMock.findAllByCurrency("USD") } returns operations
                }

                it("groups and sum operations correctly") {
                    val result = operationService.getPortfolioSummary(currency)

                    result.currency.shouldBe(currency)
                    result.positions.shouldContainExactly(
                        listOf(
                            StockPositionDTO(stockSymbol = "GOOG.US", totalCost = 2925.toBigDecimal(), totalVolume = 25.toBigDecimal()),
                            StockPositionDTO(stockSymbol = "TSLA.US", totalCost = 1050.toBigDecimal(), totalVolume = 12.toBigDecimal()),
                            StockPositionDTO(stockSymbol = "ALPH.US", totalCost = 190.toBigDecimal(), totalVolume = 95.toBigDecimal()),
                        ),
                    )
                }
            }

            describe("addOperations") {
                fun getOperationRequestDTO(): OperationRequestDTO =
                    OperationRequestDTO(
                        externalId = "external-id-1",
                        stockSymbol = "APL.US",
                        type = OperationType.BUY,
                        volume = 10.toBigDecimal(),
                        openDate = LocalDateTime.of(2019, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
                        pricePerVolume = 100.toBigDecimal(),
                        totalPrice = 1000.toBigDecimal(),
                    )

                fun getOperationsBatchRequestDTO(): OperationsBatchRequestDTO =
                    OperationsBatchRequestDTO(
                        operations =
                            listOf(
                                getOperationRequestDTO(),
                                getOperationRequestDTO().apply {
                                    externalId = "external-id-101"
                                    stockSymbol = "NVDA.US"
                                },
                            ),
                    )

                beforeEach {
                    every { operationRepositoryMock.findAllByExternalIdIn(any()) } returns operations
                    every { operationRepositoryMock.saveAll<OperationEntity>(any()) } returns listOf<OperationEntity>()
                }

                it("fetches entities for requested external ids") {
                    operationService.addOperations(getOperationsBatchRequestDTO(), currency)

                    val externalIds = slot<List<String>>()
                    verify {
                        operationRepositoryMock.findAllByExternalIdIn(capture(externalIds))
                    }

                    externalIds.captured.shouldContainExactly(listOf("external-id-1", "external-id-101"))
                }

                it("saves only new operations to the database") {
                    val batch = getOperationsBatchRequestDTO()
                    operationService.addOperations(batch, currency)

                    val newOperations = slot<List<OperationEntity>>()
                    verify {
                        operationRepositoryMock.saveAll(capture(newOperations))
                    }

                    val nvdaOperation = batch.operations!![1]
                    newOperations.captured.shouldHaveSize(1)
                    newOperations.captured[0].shouldBeEqualToComparingFields(nvdaOperation.toEntity(currency))
                }
            }
        }
    }
}
