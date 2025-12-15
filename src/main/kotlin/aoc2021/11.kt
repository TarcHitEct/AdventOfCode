package aoc2021

import readInput

private fun main() {
    val list = readInput(::main).let {
        it.trim().split("\n").map {
            it.trim().map { Spot(it.toString().toInt(), 0) }
        }
    }.toList()
    (0 until 1000).forEach {
        step(list)
        flash(list)
        list.flatten().forEach {
            if (it.alreadyFlashed) {
                it.energy = 0
            }
        }
        if (list.flatten().all { it.alreadyFlashed }) {
            println(it + 1)
            return
        }
    }
    println(list.flatten().sumOf { it.flashed })
}

fun step(list: List<List<Spot>>) {
    list.flatten().forEach {
        it.energy++
        it.alreadyFlashed = false
    }
}

private fun flash(list: List<List<Spot>>) {
    (0 until 10).forEach { y ->
        (0 until 10).forEach { x ->
            if (list[y][x].energy > 9 && !list[y][x].alreadyFlashed) {
                getNeighbours(list, x, y).forEach { it.energy++ }
                list[y][x].flashed++
                list[y][x].alreadyFlashed = true
                flash(list)
            }
        }
    }
}

class Spot(var energy: Int, var flashed: Int, var alreadyFlashed: Boolean = false)

fun getNeighbours(list: List<List<Spot>>, x: Int, y: Int): List<Spot> {
    return listOfNotNull(
        safeGet(list, x - 1, y),
        safeGet(list, x, y - 1),
        safeGet(list, x + 1, y),
        safeGet(list, x, y + 1),
        safeGet(list, x + 1, y + 1),
        safeGet(list, x - 1, y - 1),
        safeGet(list, x - 1, y + 1),
        safeGet(list, x + 1, y - 1)
    )
}

fun safeGet(list: List<List<Spot>>, x: Int, y: Int): Spot? {
    if (x >= 0 && y >= 0 && y < list.size && x < list.first().size) {
        return list[y][x]
    }
    return null
}
