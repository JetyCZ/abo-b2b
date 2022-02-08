package cz.abo.b2b.web.view.component

import java.math.BigDecimal
import java.math.RoundingMode

class ViewUtils {
    companion object {
        fun round(n: BigDecimal): Double {
            return n.setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toDouble()
        }
        fun display(n: BigDecimal): String {
            return round(n).toString()
        }
    }
}