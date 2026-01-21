package com.example.stocksheet

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

interface Loggable {
    val logger: KLogger get() = KotlinLogging.logger(this.javaClass.name)
}
