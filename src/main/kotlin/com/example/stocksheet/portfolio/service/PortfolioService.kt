package com.example.stocksheet.portfolio.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.portfolio.dto.PortfolioRequestDTO
import com.example.stocksheet.portfolio.dto.PortfolioResponseDTO
import com.example.stocksheet.portfolio.exceptions.PortfolioNameDuplicatedException
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
    fun createPortfolio(dto: PortfolioRequestDTO): PortfolioResponseDTO {
        logger.info { "Creating portfolio - ${dto.name} - for ${dto.currency} currency" }

        if (portfolioRepository.existsByName(requireNotNull(dto.name))) {
            throw PortfolioNameDuplicatedException("Portfolio with ${dto.name} already exists")
        }

        val portfolio = portfolioRepository.save(dto.toEntity())

        logger.info { "Created portfolio ${portfolio.id}" }

        return PortfolioResponseDTO(name = portfolio.name, currency = portfolio.currency, id = portfolio.id!!)
    }

    @Transactional(readOnly = true)
    fun getPortfolioList(): List<PortfolioResponseDTO> {
        logger.info { "Attempt to get portfolio list" }

        val portfolios = portfolioRepository.findAll()

        logger.info { "Retrieved ${portfolios.size} portfolio items" }

        return portfolios.map { portfolio ->
            PortfolioResponseDTO(
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
