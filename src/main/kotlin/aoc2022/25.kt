package aoc2022

import readInput
import solveA
import kotlin.math.max

private fun main() {
    val inputLines = readInput(::main).trim().lines()
    val sum = inputLines.map { SNAFUNumber(it) }.reduce { a, b -> a + b }
    solveA(::main, sum.toString())
}

class SNAFUNumber(nonCanonicalDigitsReversed: List<Int>) {
    constructor(number: String) : this(number.map {
        when (it) {
            '2' -> 2
            '1' -> 1
            '0' -> 0
            '-' -> -1
            '=' -> -2
            else -> throw IllegalStateException("Unknown digit $it")
        }
    }.reversed())

    val digits = nonCanonicalDigitsReversed.let {
        val canonicalDigits = it.toMutableList()
        var i = 0
        while (i in canonicalDigits.indices) {
            val digit = canonicalDigits[i]
            if (digit > 2) {
                canonicalDigits[i] = (digit + 2) % 5 - 2
                if (i + 1 !in canonicalDigits.indices) canonicalDigits.add(0)
                canonicalDigits[i + 1] += (digit + 2) / 5
            } else if (digit < -2) {
                canonicalDigits[i] = (digit - 2) % 5 + 2
                if (i + 1 !in canonicalDigits.indices) canonicalDigits.add(0)
                canonicalDigits[i + 1] += (digit - 2) / 5
            }
            i++
        }
        canonicalDigits
    }

    operator fun plus(other: SNAFUNumber): SNAFUNumber {
        val nDigits = max(this.digits.size, other.digits.size)
        val nonCanonicalDigits = (0 until nDigits).map {
            this.digits.getOrElse(it) { 0 } + other.digits.getOrElse(it) { 0 }
        }
        return SNAFUNumber(nonCanonicalDigits)
    }

    override fun toString(): String {
        return digits.reversed().map {
            when (it) {
                2 -> '2'
                1 -> '1'
                0 -> '0'
                -1 -> '-'
                -2 -> '='
                else -> throw IllegalStateException("Unknown digit $it")
            }
        }.joinToString("").trimStart('0')
    }
}