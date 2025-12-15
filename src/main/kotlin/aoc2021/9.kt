package aoc2021

import readInput

private fun main() {
    val list = readInput(::main).let {
        it.trim().split("\n").map {
            it.trim().toCharArray().map { it.toString().toInt() }
        }
    }.toList()

    val res = mutableListOf<Point>()
    list.forEachIndexed { y, _ ->
        list.first().forEachIndexed { x, _ ->
            if (getNeighbours(list, x, y).all { it > list[y][x] }) {
                res.add(Point(x, y))
            }
        }
    }
    val patchSizes = res.map { findPatch(list, it) }.sortedBy { -it.size }.take(3).map { it.size }
    println(patchSizes[0] * patchSizes[1] * patchSizes[2])
}

fun findPatch(list: List<List<Int>>, it: Point, visited: MutableList<Point> = mutableListOf()): List<Point> {
    if (visited.contains(it) || !(it.x >= 0 && it.y >= 0 && it.y < list.size && it.x < list.first().size) || list[it.y][it.x] == 9) {
        return visited
    }
    visited += it
    findPatch(list, Point(it.x - 1, it.y), visited)
    findPatch(list, Point(it.x, it.y - 1), visited)
    findPatch(list, Point(it.x + 1, it.y), visited)
    findPatch(list, Point(it.x, it.y + 1), visited)
    return visited
}

fun getNeighbours(list: List<List<Int>>, x: Int, y: Int): List<Int> {
    return listOfNotNull(
        safeGet(list, x - 1, y),
        safeGet(list, x, y - 1),
        safeGet(list, x + 1, y),
        safeGet(list, x, y + 1)
    )
}

fun safeGet(list: List<List<Int>>, x: Int, y: Int): Int? {
    if (x >= 0 && y >= 0 && y < list.size && x < list.first().size) {
        return list[y][x]
    }
    return null
}
