package aoc2025

import aoc2021.Point
import readInput
import solveA
import solveB
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private fun main() {
    val points = readInput(::main).trim().split("\n").map { line ->
        line.split(",").let {
            Point(it[0].toInt(), it[1].toInt())
        }
    }

    val maxArea = points.maxOf { a ->
        points.maxOf { b ->
            val l = abs(b.x - a.x) + 1
            val h = abs(b.y - a.y) + 1
            l.toLong() * h.toLong()
        }
    }
    solveA(::main, maxArea)

    val edge = hashSetOf<Point>()
    val outline = hashSetOf<Point>()
    (points + points.first()).windowed(2).forEach { line ->
        val from = line.first()
        val to = line.last()
        val length = to.getManhattanDistanceTo(from)
        val direction = (to - from) / length
        val outlineDirection = direction.rotateRight()
        edge.add(from)
        outline.add(from + outlineDirection)
        (1..length).forEach {
            val edgePoint = from + direction * it
            edge.add(edgePoint)
            outline.add(edgePoint + outlineDirection)
        }
    }
    outline.removeAll(edge)


    var maxArea2 = 0L
    points.forEach { a ->
        points.forEach { b ->
            val l = abs(b.x - a.x) + 1
            val h = abs(b.y - a.y) + 1
            val area = l.toLong() * h.toLong()
            if (area > maxArea2 && isValidRectangle(a, b, outline))
                maxArea2 = area
        }
    }
    solveB(::main, maxArea2)
}

private fun isValidRectangle(a: Point, b: Point, outline: HashSet<Point>): Boolean {
    val topleftX = min(a.x, b.x)
    val topleftY = min(a.y, b.y)
    val bottomrightX = max(a.x, b.x)
    val bottomrightY = max(a.y, b.y)
    return !outline.any {
        it.x >= topleftX && it.x <= bottomrightX && it.y <= bottomrightY && it.y >= topleftY
    }
}
