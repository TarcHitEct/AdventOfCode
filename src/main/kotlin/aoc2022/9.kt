package aoc2022

import aoc2021.Point
import readInput
import solveA
import solveB
import kotlin.math.abs

private fun main() {
    val moves = readInput(::main).let {
        Regex("(.) (\\d+)").findAll(it).map {
            Move(Direction.valueOf(it.groupValues[1]), it.groupValues[2].toInt())
        }
    }.toList()

    val board = Board(2)
    moves.forEach { board.move(it) }
    solveA(::main, board.visitedPositions.size)

    val board2 = Board(10)
    moves.forEach { board2.move(it) }
    solveB(::main, board2.visitedPositions.size)
}

enum class Direction { U, D, L, R }

data class Move(val direction: Direction, val steps: Int) {
    fun getVector() = when (direction) {
        Direction.U -> Point(0, 1)
        Direction.D -> Point(0, -1)
        Direction.L -> Point(-1, 0)
        Direction.R -> Point(1, 0)
    }
}

class Board(nrKnots: Int) {
    val knotPositions = Array(nrKnots) { Point(0, 0) }.toMutableList()
    val visitedPositions = mutableSetOf(Point(0, 0))

    fun move(move: Move) {
        repeat(move.steps) {
            knotPositions[0] += move.getVector()
            knotPositions.reduceIndexed { tailIndex, headPos, tailPos ->
                val t2hVector = headPos - tailPos
                if (abs(t2hVector.x) >= 2 || abs(t2hVector.y) >= 2) {
                    knotPositions[tailIndex] += t2hVector.manhattanNormalize()
                    if (tailIndex == knotPositions.lastIndex) {
                        visitedPositions += knotPositions[tailIndex]
                    }
                }
                knotPositions[tailIndex]
            }
        }
    }
}

fun Point.manhattanNormalize() = Point(if (x == 0) 0 else x / abs(x), if (y == 0) 0 else y / abs(y))