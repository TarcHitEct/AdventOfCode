package aoc2025

import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main).let {
        it.trim().split("\n").map {
            it.replace("R", "").replace("L", "-").toInt()
        }
    }
    var pos = 50
    var all0Stops = 0
    val stops = input.map {
        val prev = pos
        pos += it
        if (pos == 0)
            all0Stops++
        else if (pos >= 100) {
            all0Stops += pos / 100
        } else if (pos < 0) {
            if (prev != 0)
                all0Stops++
            all0Stops += -pos / 100
        }
        pos = (pos % 100 + 100) % 100
        pos
    }
    solveA(::main, stops.count { it == 0 })
    solveB(::main, all0Stops)
}