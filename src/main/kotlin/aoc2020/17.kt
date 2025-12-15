package aoc2020

import readInput

private fun main() {
    val space = readInput(::main).let {
        it.trim().split("\n").flatMapIndexed { y, s ->
            s.trim().toCharArray().mapIndexed { x, c ->
                Coord(x.toLong(), y.toLong(), 0, 0) to Cube(c == '#')
            }
        }
    }.toMap().toMutableMap()

    repeat(6) {
        space.filter { it.value.active }.forEach {
            neighbourCoords(it.key).forEach { neighbourCoord ->
                space.computeIfAbsent(neighbourCoord) { Cube(false) }.neighbours++
            }
        }
        with(space.iterator()) {
            forEach { if (!it.value.active && it.value.neighbours == 0L) remove() }
        }
        space.values.forEach {
            if (it.active && it.neighbours !in (2..3)) {
                it.active = false
            } else if (!it.active && it.neighbours == 3L) {
                it.active = true
            }
            it.neighbours = 0
        }
    }
    space.values.count { it.active }.also {
        println(it)
    }
}

data class Coord(val x: Long, val y: Long, val z: Long, val w: Long)
data class Cube(var active: Boolean, var neighbours: Long = 0)

private fun neighbourCoords(coord: Coord): List<Coord> {
    return (-1..1).flatMap { dx ->
        (-1..1).flatMap { dy ->
            (-1..1).flatMap { dz ->
                (-1..1).map { dw ->
                    Coord(coord.x + dx, coord.y + dy, coord.z + dz, coord.w + dw)
                }
            }
        }
    } - coord
}