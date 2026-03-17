package com.example.stocksheet.integration.openapi.config

import com.example.stocksheet.exceptions.dto.ErrorResponse
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus

@Configuration
class OpenApiConfig {
    @Bean
    fun globalErrorResponsesCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            if (openApi.components == null) {
                openApi.components = Components()
            }
            val resolvedSchema =
                ModelConverters
                    .getInstance()
                    .readAllAsResolvedSchema(ErrorResponse::class.java)

            openApi.components.addSchemas("ErrorResponse", resolvedSchema.schema)

            resolvedSchema.referencedSchemas?.forEach { (schemaName, schema) ->
                openApi.components.addSchemas(schemaName, schema)
            }

            val errorContent =
                Content().addMediaType(
                    "application/json",
                    MediaType().schema(Schema<Any>().`$ref`("#/components/schemas/ErrorResponse")),
                )

            val supportedErrorStatuses =
                listOf(
                    HttpStatus.BAD_REQUEST,
                    HttpStatus.NOT_FOUND,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                )

            val globalErrors =
                supportedErrorStatuses.associate { status ->
                    status.value().toString() to
                        ApiResponse()
                            .description(status.reasonPhrase)
                            .content(errorContent)
                }

            openApi.paths?.values?.forEach { pathItem ->
                pathItem.readOperations().forEach { operation ->
                    globalErrors.forEach { (statusCode, apiResponse) ->
                        if (!operation.responses.containsKey(statusCode)) {
                            operation.responses.addApiResponse(statusCode, apiResponse)
                        }
                    }
                }
            }
        }
}
