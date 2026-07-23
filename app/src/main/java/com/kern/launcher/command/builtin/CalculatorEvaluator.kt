package com.kern.launcher.command.builtin

import java.text.DecimalFormat
import kotlin.math.pow

object CalculatorEvaluator {

    fun evaluate(expression: String): String? {
        val cleanExpr = expression.trim().replace("×", "*").replace("÷", "/")
        if (cleanExpr.isEmpty()) return null

        // Check if expression contains basic math characters
        if (!cleanExpr.any { it in "+-*/%^" }) return null

        return try {
            val result = ExpressionParser(cleanExpr).parse()
            if (result.isNaN() || result.isInfinite()) null
            else {
                val formatter = DecimalFormat("#.##########")
                formatter.format(result)
            }
        } catch (e: Exception) {
            null
        }
    }

    private class ExpressionParser(private val expr: String) {
        private var pos = -1
        private var ch = -1

        private fun nextChar() {
            ch = if (++pos < expr.length) expr[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val res = parseExpression()
            if (pos < expr.length) throw RuntimeException("Unexpected trailing character: " + ch.toChar())
            return res
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> x /= parseFactor()
                    eat('%'.code) -> x %= parseFactor()
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = expr.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Unexpected character: " + ch.toChar())
            }

            if (eat('^'.code)) x = x.pow(parseFactor())

            return x
        }
    }
}
