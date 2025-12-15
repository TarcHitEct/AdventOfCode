package aoc2022

import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main).let {
        it.split("\n").map {
            it.trim().let { it.substring(0, it.length / 2) to it.substring(it.length / 2) }
        }
    }

    input.sumOf { rucksack ->
        val item = rucksack.first.toCharArray().intersect(rucksack.second.toCharArray().asIterable()).first()
        calcPrio(item)
    }.let {
        solveA(::main, it)
    }

    input.chunked(3).sumOf { group ->
        val item = group.map { it.first.toCharArray().toSet() + it.second.toCharArray().toSet() }.reduce { a, b ->
            a.intersect(b)
        }.first()
        calcPrio(item)
    }.let {
        solveB(::main, it)
    }
}

private fun calcPrio(item: Char): Int {
    return if (item.isLowerCase()) {
        item - 'a' + 1
    } else {
        item - 'A' + 27
    }
}