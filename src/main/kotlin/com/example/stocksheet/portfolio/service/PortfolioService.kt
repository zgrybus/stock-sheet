package com.example.stocksheet.portfolio.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.portfolio.dto.PortfolioListRequestDTO
import com.example.stocksheet.portfolio.dto.PortfolioListResponseDTO
import com.example.stocksheet.portfolio.dto.PortfolioResponseDTO
import com.example.stocksheet.portfolio.exceptions.PortfolioNotFoundException
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PortfolioService(
    val portfolioRepository: PortfolioRepository,
) : Loggable {
    @Transactional
    fun createPortfolio(dto: PortfolioListRequestDTO): PortfolioListResponseDTO {
        logger.info { "Creating portfolio - ${dto.name} - for ${dto.currency} currency" }

        // TODO
        // VALIDATE, WHETHER NAME IS DUPLICATED

        return portfolioRepository
            .save(dto.toEntity())
            .let {
                PortfolioListResponseDTO(name = it.name, currency = it.currency, id = it.id!!)
            }.also {
                logger.info { "Created portfolio ${it.id}" }
            }
    }

    @Transactional(readOnly = true)
    fun getPortfolioList(): List<PortfolioListResponseDTO> {
        logger.info { "Attempt to get portfolio list" }

        return portfolioRepository
            .findAll()
            .also {
                logger.info { "Retrieved ${it.size} portfolio items" }
            }.let {
                it.map { portfolio ->
                    PortfolioListResponseDTO(name = portfolio.name, currency = portfolio.currency, id = portfolio.id!!)
                }
            }
    }

    @Transactional(readOnly = true)
    fun getPortfolio(id: Long): PortfolioResponseDTO {
        logger.info { "Attempt to get portfolio with id $id" }

        return portfolioRepository
            .findById(id)
            .orElseThrow { PortfolioNotFoundException("Could not find portfolio with id $id") }
            .also { logger.info { "Retrieved ${it.id} portfolio" } }
            .let {
                PortfolioResponseDTO(name = it.name, currency = it.currency, id = it.id!!)
            }
    }
}
