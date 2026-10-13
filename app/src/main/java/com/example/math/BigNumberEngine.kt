package com.example.math

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.*

/**
 * High precision evaluation engine supporting arbitrary precision arithmetic,
 * massive numbers (BigInteger factorials, powers, sums), and 50-digit precision decimals.
 */
object BigNumberEngine {
    val MC = MathContext(50, RoundingMode.HALF_UP)

    val BIG_PI = BigDecimal("3.1415926535897932384626433832795028841971693993751", MC)
    val BIG_E  = BigDecimal("2.7182818284590452353602874713526624977572470936999", MC)
    val BIG_PHI = BigDecimal("1.6180339887498948482045868343656381177203091798057", MC)

    /**
     * Calculates exact factorial of any non-negative integer using BigInteger.
     * E.g., 50!, 100!, 500!
     */
    fun factorial(n: Long): BigInteger {
        if (n < 0) throw ArithmeticException("Factorial requiere un número no negativo")
        if (n > 5000) throw ArithmeticException("El factorial supera el límite razonable (n > 5000)")
        var result = BigInteger.ONE
        for (i in 2L..n) {
            result = result.multiply(BigInteger.valueOf(i))
        }
        return result
    }

    /**
     * Calculates exact permutations nPr(n, r) with BigInteger.
     */
    fun permutations(n: Long, r: Long): BigInteger {
        if (n < 0 || r < 0 || r > n) return BigInteger.ZERO
        var result = BigInteger.ONE
        for (i in 0 until r) {
            result = result.multiply(BigInteger.valueOf(n - i))
        }
        return result
    }

    /**
     * Calculates exact combinations nCr(n, r) with BigInteger.
     */
    fun combinations(n: Long, r: Long): BigInteger {
        if (n < 0 || r < 0 || r > n) return BigInteger.ZERO
        if (r == 0L || r == n) return BigInteger.ONE
        val k = if (r > n - r) n - r else r
        var num = BigInteger.ONE
        var den = BigInteger.ONE
        for (i in 1..k) {
            num = num.multiply(BigInteger.valueOf(n - i + 1))
            den = den.multiply(BigInteger.valueOf(i))
        }
        return num.divide(den)
    }

    /**
     * Computes high precision square root using Newton's method with MathContext.
     */
    fun sqrt(value: BigDecimal, mc: MathContext = MC): BigDecimal {
        if (value < BigDecimal.ZERO) throw ArithmeticException("Raíz cuadrada de número negativo")
        if (value == BigDecimal.ZERO) return BigDecimal.ZERO

        var x0 = BigDecimal(kotlin.math.sqrt(value.toDouble()), mc)
        if (x0 <= BigDecimal.ZERO) x0 = BigDecimal.ONE
        val two = BigDecimal(2, mc)

        // 10 iterations of Newton-Raphson are more than enough for 50 digits of precision
        for (i in 0 until 12) {
            x0 = x0.add(value.divide(x0, mc), mc).divide(two, mc)
        }
        return x0
    }

    /**
     * Formats large BigDecimal or BigInteger numbers cleanly:
     * - If integer, shows exact digits.
     * - Trims trailing zeroes.
     * - If exceeds 40 digits, shows scientific notation with total digit count note.
     */
    fun formatBigResult(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        val plain = stripped.toPlainString()

        // If it's a huge integer (e.g. > 10^30)
        if (stripped.scale() <= 0 && plain.length > 35) {
            val engineering = stripped.toEngineeringString()
            return "$plain\n(${plain.length} dígitos | $engineering)"
        }

        return plain
    }
}
