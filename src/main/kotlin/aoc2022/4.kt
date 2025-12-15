package aoc2022

import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main).let {
        Regex("(\\d*)-(\\d*),(\\d*)-(\\d*)").findAll(it).map {
            Pair(
                IntRange(it.groupValues[1].toInt(), it.groupValues[2].toInt()),
                IntRange(it.groupValues[3].toInt(), it.groupValues[4].toInt())
            )
        }
    }.toList()

    input.count {
        it.first.contains(it.second) || it.second.contains(it.first)
    }.let {
        solveA(::main, it)
    }

    input.count {
        it.first.intersects(it.second)
    }.let {
        solveB(::main, it)
    }
}

private fun IntRange.contains(other: IntRange): Boolean {
    return this.contains(other.first) && this.contains(other.last)
}

private fun IntRange.intersects(other: IntRange): Boolean {
    return this.contains(other.first) || this.contains(other.last) || other.contains(this.first) || other.contains(this.last)
}