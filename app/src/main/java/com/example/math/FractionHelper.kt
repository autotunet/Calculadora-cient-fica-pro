package com.example.math

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToLong

object FractionHelper {
    /**
     * Converts a decimal to a clean fraction string if it has a clean rational form.
     * E.g. 0.75 -> "3/4", 0.333333333333 -> "1/3", 2.5 -> "5/2"
     * Returns null if irrational or too complex (e.g. pi).
     */
    fun toFractionString(value: Double, maxDenominator: Long = 1000): String? {
        if (value.isNaN() || value.isInfinite()) return null
        if (abs(value) > 10000) return null

        val sign = if (value < 0) "-" else ""
        val x = abs(value)

        // Exact integer check
        val roundInt = x.roundToLong()
        if (abs(x - roundInt) < 1e-11) {
            return null // No need for fraction if it is already an integer
        }

        // Continued fraction approximation
        var h1 = 1L
        var h2 = 0L
        var k1 = 0L
        var k2 = 1L
        var b = x

        do {
            val a = floor(b).toLong()
            var aux = h1
            h1 = a * h1 + h2
            h2 = aux
            aux = k1
            k1 = a * k1 + k2
            k2 = aux
            b = 1.0 / (b - a)
        } while (abs(x - h1.toDouble() / k1.toDouble()) > 1e-7 && k1 < maxDenominator && !b.isInfinite() && !b.isNaN())

        if (k1 in 2..maxDenominator && abs(x - h1.toDouble() / k1.toDouble()) < 1e-7) {
            return "$sign$h1/$k1"
        }
        return null
    }
}
