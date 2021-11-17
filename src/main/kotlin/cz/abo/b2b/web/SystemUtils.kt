package cz.abo.b2b.web

import java.text.NumberFormat

open class SystemUtils {
    companion object {
        val runtime = Runtime.getRuntime()
        val oneMB = 1024*1024

        fun memory(): ArrayList<String> {

            val format: NumberFormat = NumberFormat.getInstance()

            val sb = ArrayList<String>()
            val maxMemory = runtime.maxMemory()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val used = totalMemory - freeMemory;

            sb.add("free memory: " + format.format(freeMemory / oneMB).toString() + " MB")
            sb.add("allocated memory: " + format.format(totalMemory / oneMB).toString() + " MB")
            sb.add("max memory: " + format.format(maxMemory / oneMB).toString() + " MB")
            sb.add("used memory: " + format.format(used / oneMB).toString() + " MB")

            return sb
        }
        fun usedMemory() : String {
            return ((runtime.totalMemory() - runtime.freeMemory()) / oneMB).toString() + " / " + runtime.totalMemory();
        }
    }

}