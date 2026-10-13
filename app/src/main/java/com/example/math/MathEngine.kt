package com.example.math

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

class MathEngine(private val angleUnit: AngleUnit = AngleUnit.DEG) {

    companion object {
        const val PI_VALUE = Math.PI
        const val E_VALUE = Math.E
        val PHI_VALUE = (1.0 + sqrt(5.0)) / 2.0

        /**
         * Formats double result for display cleanly:
         * - If whole number, displays without decimals.
         * - Normalizes tiny values close to zero (e.g. 1.22e-16 -> 0).
         * - Converts very large or very small numbers to scientific notation (×10^n).
         */
        fun formatResult(value: Double): String {
            if (value.isNaN()) return "Indefinido"
            if (value.isInfinite()) return if (value > 0) "∞" else "-∞"

            // Clean floating point artifacts close to zero
            val cleanVal = if (abs(value) < 1e-13) 0.0 else value

            // Check if clean integer
            val rounded = cleanVal.roundToLong()
            if (abs(cleanVal - rounded) < 1e-11 && abs(cleanVal) < 1e14) {
                return rounded.toString()
            }

            val absVal = abs(cleanVal)
            val symbols = DecimalFormatSymbols(Locale.US).apply {
                decimalSeparator = '.'
            }

            // Scientific notation for very large or tiny numbers
            return if (absVal >= 1e11 || (absVal < 1e-6 && absVal > 0.0)) {
                val sciFormat = DecimalFormat("0.########E0", symbols)
                val raw = sciFormat.format(cleanVal)
                val parts = raw.split("E")
                if (parts.size == 2) {
                    val mantissa = parts[0]
                    val exponent = parts[1].replace("+", "")
                    "$mantissa × 10^$exponent"
                } else {
                    raw
                }
            } else {
                // High precision decimal format, up to 10 decimal places, trimming trailing zeroes
                val df = DecimalFormat("#,##0.##########", symbols).apply {
                    isGroupingUsed = false
                }
                df.format(cleanVal)
            }
        }
    }

    /**
     * Evaluates an expression string with the current angle unit.
     * Throws IllegalArgumentException or ArithmeticException with friendly Spanish descriptions.
     */
    fun evaluate(rawExpression: String): Double {
        val sanitized = preprocessExpression(rawExpression)
        if (sanitized.isBlank()) return 0.0

        val parser = Parser(sanitized, angleUnit)
        val result = parser.parseExpression()
        if (parser.hasNext()) {
            throw IllegalArgumentException("Símbolo inesperado al final de la expresión")
        }
        if (result.isNaN()) {
            throw ArithmeticException("Resultado no definido")
        }
        return result
    }

    /**
     * Safely evaluates expression for live preview. Returns null if invalid or incomplete.
     */
    fun preview(rawExpression: String): Double? {
        return try {
            val trimmed = rawExpression.trim()
            if (trimmed.isEmpty()) return null
            // Don't preview if just an operator or trailing unfinished binary op
            if (trimmed.endsWith("+") || trimmed.endsWith("-") || trimmed.endsWith("×") ||
                trimmed.endsWith("÷") || trimmed.endsWith("*") || trimmed.endsWith("/") ||
                trimmed.endsWith("^") || trimmed.endsWith("(")
            ) {
                return null
            }
            evaluate(rawExpression)
        } catch (_: Exception) {
            null
        }
    }

    private fun preprocessExpression(expr: String): String {
        var s = expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace("sin⁻¹", "asin")
            .replace("cos⁻¹", "acos")
            .replace("tan⁻¹", "atan")
            .replace("cot⁻¹", "acot")
            .replace("sec⁻¹", "asec")
            .replace("csc⁻¹", "acsc")
            .replace("π", "PI")
            .replace("φ", "PHI")
            .replace("mod", "%")
            .trim()

        // Automatically close unclosed parentheses
        val openCount = s.count { it == '(' }
        val closeCount = s.count { it == ')' }
        if (openCount > closeCount) {
            s += ")".repeat(openCount - closeCount)
        }

        return s
    }

    // --- Parser Implementation using Recursive Descent ---
    private class Parser(val input: String, val angleUnit: AngleUnit) {
        var pos = 0

        fun hasNext(): Boolean {
            skipWhitespace()
            return pos < input.length
        }

        private fun skipWhitespace() {
            while (pos < input.length && input[pos].isWhitespace()) {
                pos++
            }
        }

        private fun peek(): Char? {
            skipWhitespace()
            return if (pos < input.length) input[pos] else null
        }

        private fun match(expected: Char): Boolean {
            skipWhitespace()
            if (pos < input.length && input[pos] == expected) {
                pos++
                return true
            }
            return false
        }

        // Grammar:
        // Expression = Term (( '+' | '-' ) Term)*
        // Term = Factor (( '*' | '/' | '%' ) Factor)*
        // Factor = Power ('^' Power)*
        // Power = ( '+' | '-' )* Postfix
        // Postfix = Primary ( '!' | '%' )*
        // Primary = Number | Constant | '(' Expression ')' | Function '(' Expression (',' Expression)? ')'

        fun parseExpression(): Double {
            var result = parseTerm()
            while (true) {
                when {
                    match('+') -> result += parseTerm()
                    match('-') -> result -= parseTerm()
                    else -> return result
                }
            }
        }

        private fun parseTerm(): Double {
            var result = parseFactor()
            while (true) {
                when {
                    match('*') -> result *= parseFactor()
                    match('/') -> {
                        val divisor = parseFactor()
                        if (abs(divisor) < 1e-15) {
                            throw ArithmeticException("División por cero")
                        }
                        result /= divisor
                    }
                    match('%') -> {
                        val divisor = parseFactor()
                        if (abs(divisor) < 1e-15) {
                            throw ArithmeticException("Módulo por cero")
                        }
                        result %= divisor
                    }
                    else -> {
                        // Implicit multiplication when next token is '(' or a function/constant letter
                        val p = peek()
                        if (p != null && (p == '(' || p.isLetter())) {
                            result *= parseFactor()
                        } else {
                            return result
                        }
                    }
                }
            }
        }

        private fun parseFactor(): Double {
            val base = parseUnary()
            if (match('^')) {
                val exponent = parseFactor() // Right-associative
                return base.pow(exponent)
            }
            return base
        }

        private fun parseUnary(): Double {
            skipWhitespace()
            if (match('+')) {
                return parseUnary()
            }
            if (match('-')) {
                return -parseUnary()
            }
            return parsePostfix()
        }

        private fun parsePostfix(): Double {
            var value = parsePrimary()
            while (true) {
                if (match('!')) {
                    value = factorial(value)
                } else {
                    break
                }
            }
            return value
        }

        private fun parsePrimary(): Double {
            skipWhitespace()
            val c = peek() ?: throw IllegalArgumentException("Expresión incompleta")

            if (match('(')) {
                val result = parseExpression()
                if (!match(')')) {
                    throw IllegalArgumentException("Falta paréntesis de cierre ')'")
                }
                return result
            }

            if (c.isDigit() || c == '.') {
                return parseNumber()
            }

            if (c.isLetter()) {
                return parseWord()
            }

            throw IllegalArgumentException("Carácter inesperado: '$c'")
        }

        private fun parseNumber(): Double {
            val start = pos
            while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) {
                pos++
            }
            // Check scientific notation in number: e.g. 1.23e-4 or 5E6
            if (pos < input.length && (input[pos] == 'e' || input[pos] == 'E')) {
                val ePos = pos
                pos++
                if (pos < input.length && (input[pos] == '+' || input[pos] == '-')) {
                    pos++
                }
                val expDigitsStart = pos
                while (pos < input.length && input[pos].isDigit()) {
                    pos++
                }
                if (pos == expDigitsStart) {
                    // Not a valid exponent, backtrack
                    pos = ePos
                }
            }
            val numStr = input.substring(start, pos)
            return numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Número no válido: $numStr")
        }

        private fun parseWord(): Double {
            val start = pos
            while (pos < input.length && (input[pos].isLetter() || input[pos].isDigit())) {
                pos++
            }
            val name = input.substring(start, pos)

            // Check constants (case-insensitive)
            when (name.uppercase()) {
                "PI" -> return PI_VALUE
                "E" -> return E_VALUE
                "PHI" -> return PHI_VALUE
            }

            // Function call
            if (!match('(')) {
                throw IllegalArgumentException("Se esperaba '(' después de la función '$name'")
            }

            val arg1 = parseExpression()
            var arg2: Double? = null

            if (match(',')) {
                arg2 = parseExpression()
            }

            if (!match(')')) {
                throw IllegalArgumentException("Falta ')' al cerrar la función '$name'")
            }

            return executeFunction(name, arg1, arg2)
        }

        private fun executeFunction(name: String, x: Double, y: Double?): Double {
            return when (name.lowercase()) {
                // Trigonometric
                "sin" -> {
                    val rad = angleUnit.toRadians(x)
                    val result = sin(rad)
                    normalizeTrigResult(result, x, 180.0)
                }
                "cos" -> {
                    val rad = angleUnit.toRadians(x)
                    val result = cos(rad)
                    normalizeTrigResult(result, x, 90.0)
                }
                "tan" -> {
                    val rad = angleUnit.toRadians(x)
                    if (abs(cos(rad)) < 1e-15) {
                        throw ArithmeticException("Tangente indefinida (cos = 0)")
                    }
                    tan(rad)
                }
                "cot" -> {
                    val rad = angleUnit.toRadians(x)
                    val s = sin(rad)
                    if (abs(s) < 1e-15) throw ArithmeticException("Cotangente indefinida")
                    cos(rad) / s
                }
                "sec" -> {
                    val rad = angleUnit.toRadians(x)
                    val c = cos(rad)
                    if (abs(c) < 1e-15) throw ArithmeticException("Secante indefinida")
                    1.0 / c
                }
                "csc" -> {
                    val rad = angleUnit.toRadians(x)
                    val s = sin(rad)
                    if (abs(s) < 1e-15) throw ArithmeticException("Cosecante indefinida")
                    1.0 / s
                }

                // Inverse Trigonometric
                "asin" -> {
                    if (x < -1.0 || x > 1.0) throw ArithmeticException("Dominio asin: [-1, 1]")
                    angleUnit.fromRadians(asin(x))
                }
                "acos" -> {
                    if (x < -1.0 || x > 1.0) throw ArithmeticException("Dominio acos: [-1, 1]")
                    angleUnit.fromRadians(acos(x))
                }
                "atan" -> angleUnit.fromRadians(atan(x))
                "atan2" -> {
                    val yVal = y ?: throw IllegalArgumentException("atan2 requiere dos argumentos: (y, x)")
                    angleUnit.fromRadians(atan2(x, yVal))
                }
                "acot" -> angleUnit.fromRadians(atan(1.0 / x))
                "asec" -> {
                    if (abs(x) < 1.0) throw ArithmeticException("Dominio asec: |x| >= 1")
                    angleUnit.fromRadians(acos(1.0 / x))
                }
                "acsc" -> {
                    if (abs(x) < 1.0) throw ArithmeticException("Dominio acsc: |x| >= 1")
                    angleUnit.fromRadians(asin(1.0 / x))
                }

                // Hyperbolic
                "sinh" -> sinh(x)
                "cosh" -> cosh(x)
                "tanh" -> tanh(x)
                "coth" -> {
                    if (x == 0.0) throw ArithmeticException("coth(0) indefinido")
                    1.0 / tanh(x)
                }
                "sech" -> 1.0 / cosh(x)
                "csch" -> {
                    if (x == 0.0) throw ArithmeticException("csch(0) indefinido")
                    1.0 / sinh(x)
                }
                "asinh" -> ln(x + sqrt(x * x + 1.0))
                "acosh" -> {
                    if (x < 1.0) throw ArithmeticException("Dominio acosh: x >= 1")
                    ln(x + sqrt(x * x - 1.0))
                }
                "atanh" -> {
                    if (abs(x) >= 1.0) throw ArithmeticException("Dominio atanh: |x| < 1")
                    0.5 * ln((1.0 + x) / (1.0 - x))
                }

                // Logarithmic & Exponential
                "ln" -> {
                    if (x <= 0.0) throw ArithmeticException("Dominio ln: x > 0")
                    ln(x)
                }
                "log", "log10" -> {
                    if (x <= 0.0) throw ArithmeticException("Dominio log: x > 0")
                    log10(x)
                }
                "log2" -> {
                    if (x <= 0.0) throw ArithmeticException("Dominio log2: x > 0")
                    ln(x) / ln(2.0)
                }
                "logb" -> {
                    val base = y ?: 10.0
                    if (x <= 0.0 || base <= 0.0 || base == 1.0) {
                        throw ArithmeticException("Base o valor de logb inválido")
                    }
                    ln(x) / ln(base)
                }
                "exp" -> exp(x)

                // Powers & Roots
                "sqrt" -> {
                    if (x < 0.0) throw ArithmeticException("Raíz cuadrada de número negativo")
                    sqrt(x)
                }
                "cbrt" -> {
                    if (x < 0) -(-x).pow(1.0 / 3.0) else x.pow(1.0 / 3.0)
                }
                "nroot", "root" -> {
                    val n = y ?: 2.0
                    if (n == 0.0) throw ArithmeticException("Índice de raíz no puede ser 0")
                    if (x < 0 && (n.roundToLong() % 2L != 0L)) {
                        -(-x).pow(1.0 / n)
                    } else if (x < 0) {
                        throw ArithmeticException("Raíz par de número negativo")
                    } else {
                        x.pow(1.0 / n)
                    }
                }
                "sqr" -> x * x
                "cube" -> x * x * x
                "inv" -> {
                    if (abs(x) < 1e-15) throw ArithmeticException("División por cero (1/0)")
                    1.0 / x
                }
                "pow" -> {
                    val exp = y ?: throw IllegalArgumentException("pow requiere dos argumentos")
                    x.pow(exp)
                }

                // Combinatorics & Stats
                "ncr", "comb" -> {
                    val r = y ?: throw IllegalArgumentException("nCr requiere dos argumentos: n, r")
                    combinations(x.toLong(), r.toLong()).toDouble()
                }
                "npr", "perm" -> {
                    val r = y ?: throw IllegalArgumentException("nPr requiere dos argumentos: n, r")
                    permutations(x.toLong(), r.toLong()).toDouble()
                }
                "fact" -> factorial(x)

                // Math Utilities
                "abs" -> abs(x)
                "floor" -> floor(x)
                "ceil" -> ceil(x)
                "round" -> round(x)
                "sign" -> sign(x)
                "gcd" -> {
                    val b = y?.toLong() ?: throw IllegalArgumentException("gcd requiere dos argumentos")
                    gcd(x.toLong(), b).toDouble()
                }
                "lcm" -> {
                    val b = y?.toLong() ?: throw IllegalArgumentException("lcm requiere dos argumentos")
                    lcm(x.toLong(), b).toDouble()
                }

                else -> throw IllegalArgumentException("Función desconocida: '$name'")
            }
        }

        private fun normalizeTrigResult(value: Double, angle: Double, baseAnglePeriod: Double): Double {
            if (abs(value) < 1e-14) return 0.0
            if (angleUnit == AngleUnit.DEG) {
                val mod = (abs(angle) % 360.0)
                if (abs(mod - baseAnglePeriod) < 1e-11 || abs(mod - (baseAnglePeriod + 180.0)) < 1e-11) {
                    if (abs(value) < 1e-10) return 0.0
                }
            }
            return value
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || n != floor(n)) {
                // Gamma function approximation for non-integers could be added,
                // or enforce non-negative integer for clarity:
                throw ArithmeticException("El factorial requiere un entero positivo")
            }
            if (n > 170.0) throw ArithmeticException("Desbordamiento en factorial (n > 170)")
            var res = 1.0
            val limit = n.toInt()
            for (i in 2..limit) {
                res *= i
            }
            return res
        }

        private fun combinations(n: Long, r: Long): Long {
            if (n < 0 || r < 0 || r > n) return 0L
            if (r == 0L || r == n) return 1L
            val k = if (r > n - r) n - r else r
            var res = 1L
            for (i in 1..k) {
                res = res * (n - i + 1) / i
            }
            return res
        }

        private fun permutations(n: Long, r: Long): Long {
            if (n < 0 || r < 0 || r > n) return 0L
            var res = 1L
            for (i in 0 until r) {
                res *= (n - i)
            }
            return res
        }

        private fun gcd(a: Long, b: Long): Long {
            var x = abs(a)
            var y = abs(b)
            while (y != 0L) {
                val t = y
                y = x % y
                x = t
            }
            return x
        }

        private fun lcm(a: Long, b: Long): Long {
            if (a == 0L || b == 0L) return 0L
            return abs(a * b) / gcd(a, b)
        }
    }
}
