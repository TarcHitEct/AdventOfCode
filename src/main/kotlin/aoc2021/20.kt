package aoc2021

import readInput
import solveA
import solveB

lateinit var algo: List<Boolean>
private fun main() {
    val input = readInput(::main)
    algo = input.split("\n").first().trim().map { it == '#' }
    val list = input.let {
        it.split("\n\n")[1].trim().split("\n").map { it.trim() }
    }.toList()
    val image = Image(
        list.flatMapIndexed { y, it ->
            it.toCharArray().mapIndexed { x, c ->
                Point(x, y) to (c == '#')
            }
        }.toMap()
    )
    val aImage = (1..2).fold(image) { acc, i -> acc.mapImage() }
    solveA(::main, aImage.pixels.values.count { it })
    val bImage = (1..50).fold(image) { acc, i -> acc.mapImage() }
    solveB(::main, bImage.pixels.values.count { it })
}

data class Image(val pixels: Map<Point, Boolean>, val outsideIsDark: Boolean = false) {
    val bounds: Rectangle;

    init {
        val topleft = Point(pixels.keys.minOf { it.x }, pixels.keys.minOf { it.y })
        val bottomright = Point(pixels.keys.maxOf { it.x }, pixels.keys.maxOf { it.y })
        bounds = Rectangle(topleft, bottomright)
    }

    operator fun get(p: Point): Boolean {
        return pixels[p] ?: outsideIsDark
    }

    fun getNeigbours(p: Point): List<Boolean> {
        return (-1..1).flatMap { y ->
            (-1..1).map { x ->
                this[p + Point(x, y)]
            }
        }
    }

    fun mapPixel(p: Point): Boolean {
        val algoIdx = getNeigbours(p).map { if (it) "1" else "0" }.joinToString("").toInt(2)
        return algo[algoIdx]
    }

    fun mapImage(): Image {
        return Image((bounds.topleft.x - 1..bounds.bottomright.x + 1).flatMap { x ->
            (bounds.topleft.y - 1..bounds.bottomright.y + 1).map { y ->
                Point(x, y).let { it to mapPixel(it) }
            }
        }.toMap(), !outsideIsDark)
    }
}