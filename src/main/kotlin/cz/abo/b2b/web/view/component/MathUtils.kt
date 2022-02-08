package cz.abo.b2b.web.view.component

import java.math.BigDecimal
import java.math.RoundingMode

class MathUtils {
    companion object {
        @JvmStatic
        fun toDouble(n: String): Double {
            return java.lang.Double.parseDouble( n.replace(',','.'))
        }

        @JvmStatic
        fun withoutVAT(includingVAT: Int) =
            BigDecimal(includingVAT).divide(BigDecimal(1.15), 5, RoundingMode.HALF_UP)
    }
}