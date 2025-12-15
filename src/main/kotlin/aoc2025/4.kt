package aoc2025

import aoc2021.Point
import readInput
import solveA
import solveB

private fun main() {
    val rolls = readInput(::main).let {
        it.trim().split("\n").map {
            it.trim().map { it == '@' }
        }
    }

    val grid = rolls.flatMapIndexed { x, row ->
        row.mapIndexed { y, isRoll ->
            Point(x, y) to isRoll
        }
    }.toMap()

    val count = grid.count { (pos, isRoll) ->
        isRoll && getAdjacentPoints(pos).count { grid[it] == true } < 4
    }

    solveA(::main, count)

    val grid2 = grid.toMutableMap()
    do {
        var found = false
        grid2.forEach { pos, isRoll ->
            if (isRoll && getAdjacentPoints(pos).count { grid2[it] == true } < 4) {
                grid2[pos] = false
                found = true
            }
        }
    } while (found)
    val initialRolls = grid.values.count { it }
    val finalRolls = grid2.values.count { it }
    solveB(::main, initialRolls - finalRolls)
}

private fun getAdjacentPoints(pos: Point): List<Point> {
    return (-1..1).flatMap { ox ->
        (-1..1).map { oy ->
            pos + Point(x = ox, y = oy)
        }
    } - pos
}