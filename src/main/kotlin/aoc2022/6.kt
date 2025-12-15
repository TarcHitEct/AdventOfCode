package aoc2022

import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main)
    input.toCharArray().toList().windowed(4).indexOfFirst { chars -> chars.toSet().size == 4 }.let {
        solveA(::main, it + 4)
    }
    input.toCharArray().toList().windowed(14).indexOfFirst { chars -> chars.toSet().size == 14 }.let {
        solveB(::main, it + 14)
    }
}