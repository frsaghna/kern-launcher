package com.kern.launcher.command.builtin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CalculatorEvaluatorTest {

    @Test
    fun testBasicOperatorsAndPrecedence() {
        assertEquals("14", CalculatorEvaluator.evaluate("2 + 3 * 4"))
        assertEquals("20", CalculatorEvaluator.evaluate("(2 + 3) * 4"))
        assertEquals("5", CalculatorEvaluator.evaluate("20 / 4"))
        assertEquals("7", CalculatorEvaluator.evaluate("10 - 3"))
        assertEquals("1", CalculatorEvaluator.evaluate("7 % 3"))
        assertEquals("8", CalculatorEvaluator.evaluate("2 ^ 3"))
    }

    @Test
    fun testParentheses() {
        assertEquals("24", CalculatorEvaluator.evaluate("(2 + 4) * (1 + 3)"))
        assertEquals("10", CalculatorEvaluator.evaluate("((2 + 3) * 2)"))
    }

    @Test
    fun testNestedParenthesesDepth100() {
        val depth = 100
        val expr = "(".repeat(depth) + "5 + 5" + ")".repeat(depth)
        val result = CalculatorEvaluator.evaluate(expr)
        assertEquals("10", result)
    }

    @Test
    fun testDivisionByZero() {
        assertNull(CalculatorEvaluator.evaluate("10 / 0"))
        assertNull(CalculatorEvaluator.evaluate("5 / (2 - 2)"))
    }

    @Test
    fun testOverflow() {
        val result = CalculatorEvaluator.evaluate("1e300 * 1e300")
        assertNull(result)
    }

    @Test
    fun testFloatingPointFormatting() {
        val result = CalculatorEvaluator.evaluate("0.1 + 0.2")
        assertNotNull(result)
        assertEquals("0.3", result)
    }

    @Test
    fun testNegativeNumbers() {
        assertEquals("5", CalculatorEvaluator.evaluate("-5 + 10"))
        assertEquals("-15", CalculatorEvaluator.evaluate("-5 - 10"))
    }

    @Test
    fun testUnaryMinus() {
        assertEquals("-5", CalculatorEvaluator.evaluate("-(2 + 3)"))
        assertEquals("1", CalculatorEvaluator.evaluate("3 + -2"))
    }

    @Test
    fun testEmptyExpression() {
        assertNull(CalculatorEvaluator.evaluate(""))
        assertNull(CalculatorEvaluator.evaluate("   "))
    }

    @Test
    fun testInvalidCharacters() {
        assertNull(CalculatorEvaluator.evaluate("2 + abc"))
        assertNull(CalculatorEvaluator.evaluate("foo * bar"))
    }

    @Test
    fun testExtremelyLongExpression() {
        val expr = (1..500).joinToString(" + ") { "1" }
        val result = CalculatorEvaluator.evaluate(expr)
        assertEquals("500", result)
    }

    @Test
    fun testDecimalSeparatorHandling() {
        assertEquals("3", CalculatorEvaluator.evaluate("1.5 * 2"))
        assertEquals("3.75", CalculatorEvaluator.evaluate("1.25 * 3"))
    }
}
