package aoc2025

import readInput
import solveA
import solveB
import kotlin.math.max

private fun main() {
    val input = readInput(::main)
    val ranges = Regex("(\\d+)-(\\d+)").findAll(input).map {
        LongRange(it.groupValues[1].toLong(), it.groupValues[2].toLong())
    }.toList()
    val ingredients = Regex("^(\\d+)$", RegexOption.MULTILINE).findAll(input).map {
        it.value.toLong()
    }.toList()

    val count = ingredients.count { ingredient ->
        ranges.any { it.contains(ingredient) }
    }

    solveA(::main, count)

    var mergedRanges = ranges.sortedBy { it.first }.toMutableList()
    do {
        val mergable = mergedRanges.windowed(2).find { it.first().endInclusive >= it.last().start }
        if (mergable != null) {
            val i = mergedRanges.indexOf(mergable.first())
            mergedRanges.remove(mergable.first())
            mergedRanges.remove(mergable.last())
            mergedRanges.add(
                i,
                LongRange(mergable.first().start, max(mergable.first().endInclusive, mergable.last().endInclusive))
            )
        }
    } while (mergable != null)

    solveB(::main, mergedRanges.sumOf { it.endInclusive - it.start + 1 })
}
