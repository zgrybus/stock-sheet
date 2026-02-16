package com.example.stocksheet.portfolio.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.portfolio.dto.PortfolioRequestDTO
import com.example.stocksheet.portfolio.dto.PortfolioResponseDTO
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import org.springframework.stereotype.Service

@Service
class PortfolioService(
    val portfolioRepository: PortfolioRepository,
) : Loggable {
    fun createPortfolio(dto: PortfolioRequestDTO): PortfolioResponseDTO {
        logger.info { "Creating portfolio - ${dto.name} - for ${dto.currency} currency" }

        return portfolioRepository
            .save(dto.toEntity())
            .let {
                PortfolioResponseDTO(name = it.name, currency = it.currency, id = it.id!!)
            }.also {
                logger.info { "Created portfolio ${it.id}" }
            }
    }

    fun getPortfolioList(): List<PortfolioResponseDTO> {
        logger.info { "Attempt to get portfolio list" }

        return portfolioRepository
            .findAll()
            .let {
                it.map { portfolio ->
                    PortfolioResponseDTO(name = portfolio.name, currency = portfolio.currency, id = portfolio.id!!)
                }
            }.also {
                logger.info { "Retrieved ${it.size} portfolio items" }
            }
    }
}
