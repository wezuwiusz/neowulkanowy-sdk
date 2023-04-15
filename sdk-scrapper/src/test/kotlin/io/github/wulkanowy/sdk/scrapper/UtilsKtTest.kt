package io.github.wulkanowy.sdk.scrapper

import org.junit.Assert.*
import org.junit.Test

class UtilsKtTest {

    @Test
    fun `get normalized symbol`() {
        assertEquals("warszawa", "Warszawa".getNormalizedSymbol())
        assertEquals("lodz", "Łódź".getNormalizedSymbol())
        assertEquals("tomaszowmazowiecki", "Tomaszów Mazowiecki".getNormalizedSymbol())
    }
}
