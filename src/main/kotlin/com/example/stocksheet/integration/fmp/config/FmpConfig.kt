package com.example.stocksheet.integration.fmp.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.support.HttpRequestWrapper
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

@Configuration
class FmpConfig(
    @Value("\${fmp.api.key}")
    private val apiKey: String,
    @Value("\${fmp.api.base-url}")
    private val baseUrl: String,
) {
    @Bean
    fun fmpWebClient(): RestClient =
        RestClient
            .builder()
            .baseUrl(baseUrl)
            .requestInterceptor { request, body, execution ->
                val uriWithApiKey =
                    UriComponentsBuilder
                        .fromUri(request.uri)
                        .queryParam("apikey", apiKey)
                        .build()
                        .toUri()

                val modifiedRequest =
                    object : HttpRequestWrapper(request) {
                        override fun getURI() = uriWithApiKey
                    }

                execution.execute(modifiedRequest, body)
            }.build()
}
