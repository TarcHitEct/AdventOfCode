package aoc2020

import readInput

private fun main() {
    var list = readInput(::main)
        .split("\n")
        .filter { it.isNotBlank() }
        .map { it.trim() }
    list.map { line ->
        val rowBinary = line.replace("F", "0")
            .replace("B", "1")
            .replace("L", "0")
            .replace("R", "1")
        Integer.parseInt(rowBinary, 2)
    }.sorted().also { seatIds ->
        (1 until seatIds.size).find {
            seatIds[it - 1] + 1 != seatIds[it]
        }.also {
            println(seatIds[it!!] - 1)
        }
    }
}