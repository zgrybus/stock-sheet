package com.example.stocksheet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockSheetApplication

fun main(args: Array<String>) {
    runApplication<StockSheetApplication>(*args)
}
