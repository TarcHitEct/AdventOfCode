package aoc2025

import readInput
import solveA
import solveB

private fun main() {
    val banks = readInput(::main).let {
        it.trim().split("\n").map {
            it.trim().map { it.digitToInt().toLong() }
        }
    }

    val sum = banks.sumOf {
        findMaxJoltage(it, 2)
    }
    solveA(::main, sum)

    val sum2 = banks.sumOf {
        findMaxJoltage(it, 12)
    }
    solveB(::main, sum2)
}

private fun findMaxJoltage(bank: List<Long>, size: Int): Long {
    val maxFirst = bank.take(bank.size - (size - 1)).max()
    if (size == 1) {
        return maxFirst
    }
    val indexFirst = bank.indexOf(maxFirst)
    val maxRest = findMaxJoltage(bank.drop(indexFirst + 1), size - 1)
    return (maxFirst.toString() + maxRest.toString()).toLong()
}
