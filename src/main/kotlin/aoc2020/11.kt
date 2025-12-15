package aoc2020

import readInput

private fun main() {
    var seats = readInput(::main).let {
        it.trim().split("\n").map {
            it.trim().toCharArray().toList()
        }
    }.toList()

    do {
        val newSeats = seats.mapIndexed { x, row ->
            row.mapIndexed { y, seat ->
                if (seat == 'L' && countOccupiedNeigbours2(x, y, seats) == 0) {
                    '#'
                } else if (seat == '#' && countOccupiedNeigbours2(x, y, seats) >= 5) {
                    'L'
                } else {
                    seat
                }
            }
        }
        val changed = newSeats != seats
        seats = newSeats
    } while (changed)

    seats.sumOf {
        it.count { it == '#' }
    }.also {
        print(it)
    }
}

fun countOccupiedNeigbours(x: Int, y: Int, seats: List<List<Char>>): Int {
    val xIndices = seats.indices
    val yIndices = seats.first().indices
    return (-1..1).sumOf { xOffset ->
        (-1..1).count { yOffset ->
            (xOffset != 0 || yOffset != 0)
                    && x + xOffset in xIndices
                    && y + yOffset in yIndices
                    && seats[x + xOffset][y + yOffset] == '#'
        }
    }
}

fun countOccupiedNeigbours2(x: Int, y: Int, seats: List<List<Char>>): Int {
    val xIndices = seats.indices
    val yIndices = seats.first().indices
    return (-1..1).sumOf { xOffset ->
        (-1..1).count { yOffset ->
            if (xOffset == 0 && yOffset == 0) {
                false
            } else {
                var found: Boolean? = null
                var distance = 1
                while (found == null) {
                    val checkX = x + xOffset * distance
                    val checkY = y + yOffset * distance
                    if (checkX !in xIndices || checkY !in yIndices) {
                        found = false
                    } else {
                        when (seats[checkX][checkY]) {
                            '#' -> found = true
                            'L' -> found = false
                            else -> distance++
                        }
                    }
                }
                found
            }
        }
    }
}