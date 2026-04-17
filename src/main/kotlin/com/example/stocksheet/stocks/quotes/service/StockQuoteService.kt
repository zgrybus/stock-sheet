package com.example.stocksheet.stocks.quotes.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.stocks.quotes.entity.StockQuoteEntity
import com.example.stocksheet.stocks.quotes.repository.StockQuoteRepository
import com.example.stocksheet.stocks.repository.StockRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class StockQuoteService(
    private val stockQuoteRepository: StockQuoteRepository,
    private val stockRepository: StockRepository,
) : Loggable {
    data class StockQuote(
        val symbol: String,
        val price: BigDecimal,
    )

    fun upsertStockQuotes(incomingQuotes: List<StockQuote>) {
        val symbols = incomingQuotes.map { it.symbol }
        val today = LocalDate.now()

        logger.info { "Attempting to upsert quotes for ${symbols.size} symbols: $symbols" }

        val existingStocksBySymbol =
            stockRepository
                .findAllBySymbolIn(symbols)
                .associateBy { it.symbol }

        val existingQuotesBySymbol =
            stockQuoteRepository
                .findExistingQuotesBySymbolsAndDate(symbols, today)
                .associateBy { it.stock.symbol }

        val (validQuotes, unknownQuotes) =
            incomingQuotes.partition {
                existingStocksBySymbol.containsKey(it.symbol)
            }

        if (unknownQuotes.isNotEmpty()) {
            logger.warn {
                "Skipping upsert for unknown stock symbols: ${unknownQuotes.map { it.symbol }}"
            }
        }

        val entitiesToSave =
            validQuotes.map { quote ->
                val existingQuoteEntity = existingQuotesBySymbol[quote.symbol]

                if (existingQuoteEntity != null) {
                    existingQuoteEntity.closedPrice = quote.price
                    existingQuoteEntity
                } else {
                    StockQuoteEntity(
                        closedPrice = quote.price,
                        date = today,
                        stock = existingStocksBySymbol.getValue(quote.symbol),
                    )
                }
            }

        stockQuoteRepository.saveAll(entitiesToSave)

        logger.info { "Successfully upserted ${entitiesToSave.size} stock quotes." }
    }
}
