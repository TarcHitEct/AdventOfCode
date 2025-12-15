package aoc2021

import readInput

private fun main() {
    val input = readInput(::main)
    val numbers = input
        .split("\n")
        .first()
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.toInt() }
    val boards = input.split("\n")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .drop(1)
        .chunked(5)
        .map { boardLines ->
            Board(boardLines.map { it.split(" ").filter { it.isNotBlank() }.map { Cell(it.toInt()) } })
        }

    numbers.forEach { nr ->
        val left = boards.filter { !it.won() }
        boards.forEach { it.mark(nr) }
        if (left.size == 1 && left.first().won()) {
            print(left.first().score(nr))
            return
        }
    }

}

class Board(val cells: List<List<Cell>>) {
    fun mark(number: Int) {
        cells.flatten().forEach {
            if (it.number == number) {
                it.marked = true
            }
        }
    }

    fun won(): Boolean {
        return cells.any { it.all { it.marked } } ||
                (0 until 5).any { x ->
                    (0 until 5).all { y ->
                        cells[y][x].marked
                    }
                }
    }

    fun score(lastNr: Int): Int {
        return cells.flatten().filter { !it.marked }.sumOf { it.number } * lastNr
    }
}

data class Cell(var number: Int, var marked: Boolean = false)