package com.example.stocksheet.integration.fmp.service

import com.example.stocksheet.integration.fmp.config.FmpConfig
import com.example.stocksheet.integration.fmp.dto.FmpCompanyProfileResponseDTO
import org.springframework.stereotype.Service
import org.springframework.web.client.body

@Service
class FmpService(
    private val fmpConfig: FmpConfig,
) {
    companion object {
        const val COMPANY_PROFILE_URL = "/profile"
    }

    fun getCompanyProfile(symbol: String): FmpCompanyProfileResponseDTO? =
        fmpConfig
            .fmpWebClient()
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .path(
                        COMPANY_PROFILE_URL,
                    ).queryParam("symbol", symbol)
                    .build()
            }.retrieve()
            .body<List<FmpCompanyProfileResponseDTO>>()
            ?.firstOrNull()
}
