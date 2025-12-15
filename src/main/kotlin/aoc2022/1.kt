package aoc2022

import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main).let {
        it.split("\n\n").map {
            it.trim().split("\n").map {
                it.toInt()
            }
        }
    }
    solveA(::main, input.map { it.sum() }.max())
    solveB(::main, input.map { it.sum() }.sortedDescending().take(3).sum())
}