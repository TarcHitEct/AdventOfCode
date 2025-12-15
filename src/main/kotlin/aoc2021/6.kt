package aoc2021

import readInput

private fun main() {
    val list = readInput(::main).let {
        it.trim().split(",").map { it.toLong() }
    }.toList()
    var byState = list.groupBy { it }.mapValues { it.value.count().toLong() }
    (0 until 256).forEach {
        byState = nextDay(byState)
    }
    println(byState.values.sum())
}

fun nextDay(byState: Map<Long, Long>): Map<Long, Long> {
    val result = mutableMapOf<Long, Long>()
    byState.forEach { (state, count) ->
        if (state == 0L) {
            result.compute(6) { _, oldVal -> (oldVal ?: 0) + count }
            result.compute(8) { _, oldVal -> (oldVal ?: 0) + count }
        } else {
            result.compute(state - 1) { _, oldVal -> (oldVal ?: 0) + count }
        }
    }
    return result
}
