package aoc2025

import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main).let {
        it.trim().split(",").map {
            val split = it.split("-")
            LongRange(split[0].toLong(), split[1].toLong() + 1)
        }
    }

    val invalid = input.flatMap { range ->
        range.filter { id ->
            isInvalid(id, id.toString().length / 2)
        }
    }
    solveA(::main, invalid.sum())

    val invalid2 = input.flatMap { range ->
        range.filter { id ->
            (1..id.toString().length / 2).any { isInvalid(id, it) }
        }
    }
    solveB(::main, invalid2.sum())
}

private fun isInvalid(id: Long, patternLength: Int): Boolean {
    val str = id.toString()
    if (patternLength == 0 || str.length % patternLength != 0) return false
    val chunks = str.chunked(patternLength)
    return chunks.size >= 2 && chunks.all { it == chunks.first() }
}