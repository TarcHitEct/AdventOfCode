package aoc2021

import readInput
import kotlin.math.abs

private fun main() {
    val list = readInput(::main).let {
        it.trim().split(",").map { it.toLong() }
    }.toList().sorted()
    val cost = (list.first()..list.last()).minOf { pos ->
        list.sumOf {
            val moves = abs(it - pos)
            (moves + 1) * moves / 2
        }
    }
    println(cost)
}