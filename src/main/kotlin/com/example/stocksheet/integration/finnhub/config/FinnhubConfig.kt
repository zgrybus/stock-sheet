package com.example.stocksheet.integration.finnhub.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class FinnhubConfig(
    @Value("\${spring.finnhub.api.key}")
    private val apiKey: String,
    @Value("\${finnhub.api.base-url}")
    private val baseUrl: String,
) {
    @Bean
    fun finnhubWebClient(): RestClient =
        RestClient
            .builder()
            .baseUrl(baseUrl)
            .defaultHeader("X-Finnhub-Token", apiKey)
            .build()
}
