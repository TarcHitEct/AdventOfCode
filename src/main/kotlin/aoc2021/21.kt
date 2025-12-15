package aoc2021

import readInput
import solveA
import solveB

private fun main() {
    mainA()
    mainB()
}

private fun mainA() {
    val input = readInput(::main)
    val p1Pos = input.split("\n").first().split(" ").last().toInt() - 1
    val p2Pos = input.split("\n").last().split(" ").last().toInt() - 1
    val score = mutableMapOf(0 to 0, 1 to 0)
    val pos = mutableMapOf(0 to p1Pos, 1 to p2Pos)
    var curPlayer = 0
    val die = DeterministicDie()

    while (score.values.maxOrNull()!! < 1000) {
        pos[curPlayer] = pos[curPlayer]!! + die.roll()
        pos[curPlayer] = pos[curPlayer]!! + die.roll()
        pos[curPlayer] = (pos[curPlayer]!! + die.roll()) % 10
        score[curPlayer] = score[curPlayer]!! + pos[curPlayer]!! + 1
        curPlayer = (curPlayer + 1) % 2
    }
    solveA(::main, score.values.minOrNull()!! * die.nrRolls)
}

private fun mainB() {
    val input = readInput(::main)
    val p1Pos = input.split("\n").first().split(" ").last().toLong() - 1
    val p2Pos = input.split("\n").last().split(" ").last().toLong() - 1
    val score = mapOf(0 to 0L, 1 to 0L)
    val pos = mapOf(0 to p1Pos, 1 to p2Pos)
    val curPlayer = 0
    val wins = mutableMapOf(0 to 0L, 1 to 0L)

    quantumRoll(pos, score, curPlayer, wins, 1)
    solveB(::main, wins.values.maxOrNull()!!)
}

fun quantumRoll(
    pos: Map<Int, Long>,
    score: Map<Int, Long>,
    curPlayer: Int,
    wins: MutableMap<Int, Long>,
    nrUniverses: Long
) {
    score.entries.find { it.value >= 21 }?.let {
        wins[it.key] = wins[it.key]!! + nrUniverses
        return
    }
    val newPlayer = (curPlayer + 1) % 2
    threeRollOutcomes.forEach { (combinedRoll, times) ->
        val newPos = pos.mapValues { if (it.key == curPlayer) (it.value + combinedRoll) % 10 else it.value }
        val newScore = score.mapValues { if (it.key == curPlayer) (it.value + newPos[curPlayer]!! + 1) else it.value }
        quantumRoll(newPos, newScore, newPlayer, wins, nrUniverses * times)
    }
}

val threeRollOutcomes = (1..3).flatMap { a ->
    (1..3).flatMap { b ->
        (1..3).map { c ->
            a + b + c
        }
    }
}.groupBy { it }.mapValues { it.value.size }

class DeterministicDie() {
    var nrRolls = 0

    fun roll(): Int {
        return nrRolls++ % 100 + 1
    }
}