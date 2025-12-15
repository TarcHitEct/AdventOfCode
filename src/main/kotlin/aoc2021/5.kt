package aoc2021

import readInput
import kotlin.math.abs

private fun main() {
    val list = readInput(::main).let {
        it.trim().split("\n").map {
            Regex("(\\d*),(\\d*) -> (\\d*),(\\d*)").find(it)?.let {
                Line(
                    Point(it.groupValues[1].toInt(), it.groupValues[2].toInt()),
                    Point(it.groupValues[3].toInt(), it.groupValues[4].toInt())
                )
            }
        }
    }.toList().filterNotNull()

    val space = mutableMapOf<Point, Int>()
    list.forEach {
        it.getPoints().forEach {
            val nr = space.getOrDefault(it, 0)
            space[it] = nr + 1
        }
    }
    println(space.values.count { it >= 2 })
}

class Line(val from: Point, val to: Point) {
    fun getPoints(): List<Point> {
        val dx = Math.abs(from.x - to.x)
        val dy = Math.abs(from.y - to.y)
        val len = Math.max(dx, dy)
        val dir = to - from
        return (0..len).map {
            from + dir * it / len
        }
    }
}

data class Point(val x: Int, val y: Int) {
    operator fun plus(v2: Point): Point {
        return Point(x + v2.x, y + v2.y)
    }

    operator fun minus(v2: Point): Point {
        return Point(x - v2.x, y - v2.y)
    }

    operator fun times(f: Int): Point {
        return Point(x * f, y * f)
    }

    operator fun div(f: Int): Point {
        return Point(x / f, y / f)
    }

    fun rotateRight(): Point {
        return Point(y, -x)
    }

    fun rotateLeft(): Point {
        return Point(-y, x)
    }

    fun getManhattanDistanceTo(other: Point): Int {
        return (this - other).let { abs(it.x) + abs(it.y) }
    }
}