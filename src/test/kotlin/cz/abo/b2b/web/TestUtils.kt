package cz.abo.b2b.web

import java.math.BigDecimal
import kotlin.test.assertEquals

class TestUtils {
    companion object {
        fun assertBigDecimal(expected: BigDecimal, actual:BigDecimal) {
            assertEquals(expected.toDouble(), actual.toDouble(), 0.0001)
        }
        fun assertBigDecimal(expected: Double, actual:BigDecimal) {
            assertEquals(expected, actual.toDouble(), 0.0001)
        }
    }
}