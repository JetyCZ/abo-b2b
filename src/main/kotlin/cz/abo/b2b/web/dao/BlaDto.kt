package cz.abo.b2b.web.dao

import java.math.BigDecimal
import java.math.RoundingMode

class BlaDto{
    var name : BigDecimal = BigDecimal.ZERO
        //if you need to overwrite the getter:
        get() = field
        // if you need to overwrite the setter:
        set(value) { field = value.setScale(3, RoundingMode.HALF_UP) }


}
