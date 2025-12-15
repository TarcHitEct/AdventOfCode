package aoc2022

import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main).let {
        it.split("\n").map {
            it.trim().split(" ").let {
                Pair(it[0][0].code - 'A'.code, it[1][0].code - 'X'.code)
            }
        }
    }

    input.sumOf { (opponent, mine) ->
        calcScore(mine, opponent)
    }.let {
        solveA(::main, it)
    }

    input.sumOf { (opponent, outcome) ->
        val mine = when (outcome) {
            0 -> (opponent + 2) % 3
            1 -> opponent
            2 -> (opponent + 1) % 3
            else -> throw IllegalArgumentException("Invalid Input $outcome")
        }
        calcScore(mine, opponent)
    }.let {
        solveB(::main, it)
    }
}

private fun calcScore(mine: Int, opp: Int) = when (mine) {
    (opp + 1) % 3 -> 6 + mine + 1
    opp -> 3 + mine + 1
    else -> 0 + mine + 1
}