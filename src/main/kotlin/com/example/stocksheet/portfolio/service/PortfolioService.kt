package com.example.stocksheet.portfolio.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.portfolio.dto.PortfolioListRequestDTO
import com.example.stocksheet.portfolio.dto.PortfolioListResponseDTO
import com.example.stocksheet.portfolio.dto.PortfolioResponseDTO
import com.example.stocksheet.portfolio.exceptions.PortfolioNotFoundException
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import org.springframework.data.repository.findByIdOrNull
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

        val portfolio = portfolioRepository.save(dto.toEntity())

        logger.info { "Created portfolio ${portfolio.id}" }

        return PortfolioListResponseDTO(name = portfolio.name, currency = portfolio.currency, id = portfolio.id!!)
    }

    @Transactional(readOnly = true)
    fun getPortfolioList(): List<PortfolioListResponseDTO> {
        logger.info { "Attempt to get portfolio list" }

        val portfolios = portfolioRepository.findAll()

        logger.info { "Retrieved ${portfolios.size} portfolio items" }

        return portfolios.map { portfolio ->
            PortfolioListResponseDTO(
                id = portfolio.id!!,
                name = portfolio.name,
                currency = portfolio.currency,
            )
        }
    }

    @Transactional(readOnly = true)
    fun getPortfolio(id: Long): PortfolioResponseDTO {
        logger.info { "Attempt to get portfolio with id $id" }

        val portfolio = portfolioRepository.findByIdOrNull(id) ?: throw PortfolioNotFoundException("Could not find portfolio with id $id")

        logger.info { "Retrieved ${portfolio.id} portfolio" }

        return PortfolioResponseDTO(name = portfolio.name, currency = portfolio.currency, id = portfolio.id!!)
    }

    @Transactional()
    fun deletePortfolio(id: Long) {
        logger.info { "Attempt to delete portfolio with id $id" }

        val portfolio = portfolioRepository.findByIdOrNull(id) ?: throw PortfolioNotFoundException("Could not find portfolio with id $id")

        portfolioRepository.delete(portfolio)

        logger.info { "Deleted $id portfolio" }
    }
}
