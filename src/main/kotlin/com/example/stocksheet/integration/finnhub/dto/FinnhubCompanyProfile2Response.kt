package com.example.stocksheet.integration.finnhub.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class FinnhubCompanyProfile2Response(
    @JsonProperty("name") val name: String?,
    @JsonProperty("exchange") val exchange: String?,
    @JsonProperty("finnhubIndustry") val industry: String?,
    @JsonProperty("ticker") val ticker: String?,
    @JsonProperty("price") val price: BigDecimal? = BigDecimal.ZERO,
    @JsonProperty("dividend") val dividend: BigDecimal? = BigDecimal.ZERO,
    @JsonProperty("dividendFrequency") val dividendFrequency: String?,
)
