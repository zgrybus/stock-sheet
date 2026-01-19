package com.example.stocksheet

import io.kotest.matchers.shouldBe

class SimpleTest : BaseIntegrationTest() {
    init {
        describe("Simple test") {
            it("returns 4") {
                val result = 2 + 2
                result.shouldBe(4)
            }
        }
    }
}
