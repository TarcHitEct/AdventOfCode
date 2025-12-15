package aoc2022

import GraphEdge
import GraphNode
import reachability
import readInput
import solveA
import solveB
import kotlin.math.pow
import kotlin.math.sqrt

private fun main() {
    val points = readInput(::main).let {
        Regex("(\\d+),(\\d+),(\\d+)").findAll(it).map {
            Point3D(it.groupValues[1].toInt(), it.groupValues[2].toInt(), it.groupValues[3].toInt())
        }.toList()
    }

    points.sumOf { point ->
        point.getNeighbours().filter {
            it !in points
        }.size
    }.let {
        solveA(::main, it)
    }

    val grid = Grid(points)
    points.sumOf { point ->
        point.getNeighbours().filter {
            grid.isExteriorPoint(it)
        }.size
    }.let {
        solveB(::main, it)
    }
}

class Grid(val points: List<Point3D>) {
    val startX = -1
    val startY = -1
    val startZ = -1
    val endX = points.maxOf { it.x } + 1
    val endY = points.maxOf { it.y } + 1
    val endZ = points.maxOf { it.z } + 1
    val reachabilityResult = GridPoint(Point3D(-1, -1, -1)).reachability()

    fun isExteriorPoint(point: Point3D) = reachabilityResult.reachableNodes.contains(GridPoint(point))

    inner class GridPoint(val position: Point3D) : GraphNode<GridPoint> {
        override val edges
            get() = position.getNeighbours().filter {
                it !in points &&
                        it.x >= startX && it.x <= endX &&
                        it.y >= startY && it.y <= endY &&
                        it.z >= startZ && it.z <= endZ
            }.map { point -> GraphEdge(this, GridPoint(point)) }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GridPoint

            if (position != other.position) return false

            return true
        }

        override fun hashCode(): Int {
            return position.hashCode()
        }
    }
}


data class Point3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(v2: Point3D) = Point3D(x + v2.x, y + v2.y, z + v2.z)
    operator fun minus(v2: Point3D) = Point3D(x - v2.x, y - v2.y, z - v2.z)
    operator fun times(f: Int) = Point3D(x * f, y * f, z * f)
    operator fun div(f: Int) = Point3D(x / f, y / f, z / f)
    fun getNeighbours() = listOf(
        Point3D(x + 1, y, z), Point3D(x - 1, y, z),
        Point3D(x, y + 1, z), Point3D(x, y - 1, z),
        Point3D(x, y, z + 1), Point3D(x, y, z - 1)
    )

    fun distanceTo(b: Point3D): Double {
        return sqrt(
            (this.x - b.x).toDouble().pow(2)
                    + (this.y - b.y).toDouble().pow(2)
                    + (this.z - b.z).toDouble().pow(2)
        )
    }
}
