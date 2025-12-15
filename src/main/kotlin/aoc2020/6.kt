package aoc2020

import readInput

private fun main() {
    var list = readInput(::main).trim()
        .split("\n\n")
        .map { it.trim().split("\n").map { it.trim().toCharArray().toSet() } }

    list.sumOf {
        it.reduce { a, b ->
            a.intersect(b)
        }.size
    }.also { println(it) }
}