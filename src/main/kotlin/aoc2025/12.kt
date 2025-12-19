package aoc2025

import readInput
import solveA

private fun main() {
    val input = readInput(::main).trim()
    val shapes = Regex(""":\s(([#.]+\s)+)""").findAll(input).map {
        it.groupValues[1].trim().split("\n").map {
            it.map { it == '#' }
        }
    }.toList()
    val regions = Regex("""(\d+)x(\d+): ([\d ]*)""").findAll(input).map {
        val x = it.groupValues[1].toInt()
        val y = it.groupValues[2].toInt()
        val counts = it.groupValues[3].split(" ").map { it.toInt() }
        Region(x, y, counts)
    }.toList()

    val easy = regions.count { region ->
        val x = region.sizeX / 3
        val y = region.sizeY / 3
        x * y >= region.shapeCounts.sum()
    }

    val nope = regions.count { region ->
        shapes.mapIndexed { i, shape ->
            shape.sumOf { it.count { it } } * region.shapeCounts[i]
        }.sum() > region.sizeX * region.sizeY
    }

    assert(easy + nope == regions.size)
    solveA(::main, easy)
}

private data class Region(val sizeX: Int, val sizeY: Int, val shapeCounts: List<Int>)