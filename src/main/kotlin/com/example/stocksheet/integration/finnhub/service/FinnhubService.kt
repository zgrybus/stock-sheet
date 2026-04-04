package com.example.stocksheet.integration.finnhub.service

import com.example.stocksheet.integration.finnhub.config.FinnhubConfig
import com.example.stocksheet.integration.finnhub.dto.FinnhubCompanyProfile2Response
import com.example.stocksheet.integration.finnhub.dto.FinnhubSymbolLookupResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.body

@Service
class FinnhubService(
    private val finnhubConfig: FinnhubConfig,
) {
    companion object {
        const val COMPANY_PROFILE_2_URL = "/stock/profile2"
        const val SYMBOL_LOOKUP = "/search"
    }

    fun getCompanyProfile2(symbol: String): FinnhubCompanyProfile2Response? =
        finnhubConfig
            .finnhubWebClient()
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .path(
                        COMPANY_PROFILE_2_URL,
                    ).queryParam("symbol", symbol)
                    .build()
            }.retrieve()
            .body<FinnhubCompanyProfile2Response>()

    fun getSymbolLookup(
        symbol: String,
        exchange: String,
    ): FinnhubSymbolLookupResponse.FinnhubSymbolLookupType {
        val response =
            finnhubConfig
                .finnhubWebClient()
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path(SYMBOL_LOOKUP)
                        .queryParam("q", symbol)
                        .queryParam("exchange", exchange)
                        .build()
                }.retrieve()
                .body<FinnhubSymbolLookupResponse>()

        return response?.result?.firstOrNull()?.type ?: FinnhubSymbolLookupResponse.FinnhubSymbolLookupType.UNKNOWN
    }
}
