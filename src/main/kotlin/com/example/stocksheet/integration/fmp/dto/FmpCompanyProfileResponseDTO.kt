package com.example.stocksheet.integration.fmp.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class FmpCompanyProfileResponseDTO(
    @JsonProperty("symbol") val symbol: String,
    @JsonProperty("price") val price: BigDecimal,
    @JsonProperty("companyName") val name: String,
    @JsonProperty("exchange") val exchange: String,
    @JsonProperty("industry") val industry: String,
    @JsonProperty("lastDividend") val lastDividend: BigDecimal,
)
