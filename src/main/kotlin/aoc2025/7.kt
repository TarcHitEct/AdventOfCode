package aoc2025

import aoc2021.Point
import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main).trim().split("\n").map {
        it.toCharArray()
    }.toTypedArray()

    var splits = 0
    val paths = mutableMapOf<Point, Long>()
    input.forEachIndexed { x, row ->
        if (x == 0) return@forEachIndexed
        row.forEachIndexed { y, field ->
            val above = input[x - 1][y]
            val pathsAbove = if (above == 'S') 1 else paths[Point(x - 1, y)] ?: 0
            if (field == '.' && (above == 'S' || above == '|')) {
                input[x][y] = '|'
                paths.compute(Point(x, y)) { _, p ->
                    (p ?: 0) + pathsAbove
                }
            }
            if (field == '^' && above == '|') {
                input[x][y - 1] = '|'
                input[x][y + 1] = '|'
                splits++
                val pathsAboveR = paths[Point(x - 1, y + 1)] ?: 0
                paths.compute(Point(x, y - 1)) { _, p ->
                    (p ?: 0) + pathsAbove
                }
                paths.compute(Point(x, y + 1)) { _, p ->
                    (p ?: 0) + pathsAbove + pathsAboveR
                }
            }
        }
    }

    solveA(::main, splits)
    solveB(::main, paths.filter { it.key.x == input.lastIndex }.values.sum())
}
