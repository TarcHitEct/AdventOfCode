package aoc2020

import readInput
import java.math.BigInteger

private fun main() {
    val input = readInput(::main).trim().split("\n")
    val arrival = input[0].trim().toBigInteger()
    val intervals = input[1].trim().split(",").map { if (it.trim() != "x") it.trim().toBigInteger() else null }
    intervals.filterNotNull().minByOrNull {
        it - (arrival % it)
    }!!.also {
        println(it * (it - (arrival % it)))
    }

    var start = BigInteger.ZERO
    var stepSize = intervals[0]!!
    intervals.forEachIndexed { index, interval ->
        if (interval != null) {
            if ((start + index.toBigInteger()) % interval != BigInteger.ZERO) {
                while ((start + index.toBigInteger()) % interval != BigInteger.ZERO) {
                    start += stepSize
                }
                stepSize *= interval
            }
        }
    }
    intervals.forEachIndexed { index, interval ->
        if (interval != null) {
            println("($start + $index) % $interval = ${(start + index.toBigInteger()) % interval}")
        }
    }
}