package com.example

import com.example.math.AngleUnit
import com.example.math.FractionHelper
import com.example.math.MathEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import kotlin.math.abs

class MathEngineTest {

    @Test
    fun testBasicArithmetic() {
        val engine = MathEngine()
        assertEquals(7.0, engine.evaluate("3 + 4"), 1e-9)
        assertEquals(10.0, engine.evaluate("2 + 2 * 4"), 1e-9)
        assertEquals(16.0, engine.evaluate("(2 + 2) * 4"), 1e-9)
        assertEquals(2.5, engine.evaluate("5 / 2"), 1e-9)
    }

    @Test
    fun testTrigonometryDegreeMode() {
        val degEngine = MathEngine(AngleUnit.DEG)
        assertEquals(0.5, degEngine.evaluate("sin(30)"), 1e-9)
        assertEquals(1.0, degEngine.evaluate("sin(90)"), 1e-9)
        assertEquals(0.0, degEngine.evaluate("sin(180)"), 1e-9)
        assertEquals(0.0, degEngine.evaluate("cos(90)"), 1e-9)
        assertEquals(1.0, degEngine.evaluate("tan(45)"), 1e-9)
        assertEquals(45.0, degEngine.evaluate("atan(1)"), 1e-9)
        assertEquals(30.0, degEngine.evaluate("asin(0.5)"), 1e-9)
    }

    @Test
    fun testTrigonometryRadianMode() {
        val radEngine = MathEngine(AngleUnit.RAD)
        assertEquals(0.0, radEngine.evaluate("sin(π)"), 1e-9)
        assertEquals(1.0, radEngine.evaluate("sin(π / 2)"), 1e-9)
        assertEquals(-1.0, radEngine.evaluate("cos(π)"), 1e-9)
    }

    @Test
    fun testHyperbolicFunctions() {
        val engine = MathEngine()
        assertEquals(0.0, engine.evaluate("sinh(0)"), 1e-9)
        assertEquals(1.0, engine.evaluate("cosh(0)"), 1e-9)
        assertEquals(0.0, engine.evaluate("tanh(0)"), 1e-9)
        val asinhVal = engine.evaluate("asinh(0)")
        assertEquals(0.0, asinhVal, 1e-9)
    }

    @Test
    fun testLogarithmicAndExponential() {
        val engine = MathEngine()
        assertEquals(1.0, engine.evaluate("ln(e)"), 1e-9)
        assertEquals(2.0, engine.evaluate("log(100)"), 1e-9)
        assertEquals(3.0, engine.evaluate("log2(8)"), 1e-9)
        assertEquals(8.0, engine.evaluate("2^3"), 1e-9)
        assertEquals(1000.0, engine.evaluate("10^3"), 1e-9)
    }

    @Test
    fun testRootsAndPowers() {
        val engine = MathEngine()
        assertEquals(5.0, engine.evaluate("sqrt(25)"), 1e-9)
        assertEquals(3.0, engine.evaluate("cbrt(27)"), 1e-9)
        assertEquals(9.0, engine.evaluate("3^2"), 1e-9)
    }

    @Test
    fun testCombinatoricsAndFactorial() {
        val engine = MathEngine()
        assertEquals(120.0, engine.evaluate("5!"), 1e-9)
        assertEquals(10.0, engine.evaluate("nCr(5, 2)"), 1e-9)
        assertEquals(20.0, engine.evaluate("nPr(5, 2)"), 1e-9)
    }

    @Test
    fun testImplicitMultiplication() {
        val engine = MathEngine(AngleUnit.DEG)
        assertEquals(2.0 * Math.PI, engine.evaluate("2π"), 1e-9)
        assertEquals(15.0, engine.evaluate("3(5)"), 1e-9)
        assertEquals(35.0, engine.evaluate("(2+3)(4+3)"), 1e-9)
        assertEquals(1.0, engine.evaluate("2sin(30)"), 1e-9)
    }

    @Test
    fun testFractionHelper() {
        assertEquals("3/4", FractionHelper.toFractionString(0.75))
        assertEquals("1/3", FractionHelper.toFractionString(1.0 / 3.0))
        assertEquals("5/2", FractionHelper.toFractionString(2.5))
    }

    @Test
    fun testResultFormatting() {
        assertEquals("5", MathEngine.formatResult(5.0))
        assertEquals("0.5", MathEngine.formatResult(0.5))
        assertEquals("0", MathEngine.formatResult(0.0))
    }

    @Test
    fun testBigNumberEngine() {
        val fact50 = com.example.math.BigNumberEngine.factorial(50)
        assert(fact50.toString().length > 60)
        assert(fact50.toString().endsWith("000000000000"))

        val comb = com.example.math.BigNumberEngine.combinations(50, 6)
        assertEquals(java.math.BigInteger.valueOf(15890700L), comb)
    }
}
