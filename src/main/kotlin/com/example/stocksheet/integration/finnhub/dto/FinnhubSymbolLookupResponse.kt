package com.example.stocksheet.integration.finnhub.dto

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonProperty

data class FinnhubSymbolLookupResponse(
    @JsonProperty("count") val count: Int,
    @JsonProperty("result") val result: List<SymbolResult>,
) {
    data class SymbolResult(
        @JsonProperty("type") val type: FinnhubSymbolLookupType?,
    )

    enum class FinnhubSymbolLookupType {
        @JsonProperty("Common Stock")
        CommonStock,

        @JsonProperty("ETP")
        ETP,

        @JsonEnumDefaultValue
        UNKNOWN,
    }
}
