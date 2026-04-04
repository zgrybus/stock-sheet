package com.example.stocksheet.integration.finnhub.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class FinnhubCompanyProfile2Response(
    @JsonProperty("name") val name: String?,
    @JsonProperty("exchange") val exchange: String?,
    @JsonProperty("finnhubIndustry") val industry: String?,
    @JsonProperty("ticker") val ticker: String?,
)
