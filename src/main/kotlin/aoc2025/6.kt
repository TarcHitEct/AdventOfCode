package aoc2025

import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main).split("\n").dropLast(1)
    val numbers = input.dropLast(1).map {
        Regex("""\d+""").findAll(it).map { it.value.toLong() }.toList()
    }
    val problems = numbers.first().indices.map { i ->
        numbers.map { it[i] }
    }
    val operators = input.last().let {
        Regex("""[+*]""").findAll(it).map { it.value }.toList()
    }

    val results = problems.mapIndexed { index, operands ->
        val operation: Long.(Long) -> Long = if (operators[index] == "+")
            Long::plus
        else
            Long::times
        operands.reduce { a, b -> a.operation(b) }
    }

    solveA(::main, results.sum())

    val problems2 = input.dropLast(1).let {
        it.first().indices.map { i ->
            val str = it.map { row -> row[i] }.joinToString("").trim()
            if (str != "") {
                str.toLong()
            } else {
                null
            }
        }
    }.let {
        it.fold(mutableListOf(mutableListOf<Long>())) { acc, number ->
            if (number == null) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(number)
            }
            acc
        }
    }
    val results2 = problems2.mapIndexed { index, operands ->
        val operation: Long.(Long) -> Long = if (operators[index] == "+")
            Long::plus
        else
            Long::times
        operands.reduce { a, b -> a.operation(b) }
    }
    solveB(::main, results2.sum())
}
