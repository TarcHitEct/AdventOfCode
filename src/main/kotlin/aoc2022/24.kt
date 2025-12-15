package aoc2022

import GraphEdge
import GraphNode
import aStar
import aoc2021.Point
import lcm
import readInput
import solveA
import solveB

private fun main() {
    val test = """
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """.trimIndent()
    val inputLines = readInput(::main).trim().lines().drop(1).dropLast(1).map {
        it.drop(1).dropLast(1)
    }
    val valley = Valley(inputLines)
    val t1 = System.currentTimeMillis()
    val d1 = valley.getShortestDistance(valley.start, valley.end, 0)
    val d2 = valley.getShortestDistance(valley.end, valley.start, d1)
    val d3 = valley.getShortestDistance(valley.start, valley.end, d1 + d2)
    val t2 = System.currentTimeMillis()
    println("Took ${(t2 - t1) / 1000}s")
    solveA(::main, d1)
    solveB(::main, d1 + d2 + d3)
}

class Valley(inputLines: List<String>) {
    val blizzards = inputLines.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c != '.') {
                Blizzard(
                    Point(x, y), when (c) {
                        '>' -> Direction.R
                        'v' -> Direction.D
                        '<' -> Direction.L
                        '^' -> Direction.U
                        else -> throw IllegalStateException("Unknown direction $c")
                    }
                )
            } else {
                null
            }
        }
    }
    val width = inputLines.first().length
    val height = inputLines.size
    val start = Point(0, -1)
    val end = Point(width - 1, height)

    fun getShortestDistance(from: Point, to: Point, startTime: Int): Int {
        return Step(from, to, startTime).aStar().shortestDistance
    }

    fun getBlizzardsAtTime(time: Int) = blizzards.map { it.getPositionAtTime(time) }

    inner class Blizzard(val point: Point, val direction: Direction) {
        fun getPositionAtTime(time: Int): Point {
            return when (direction) {
                Direction.U -> Point(point.x, wrapIndex((point.y - time).toLong(), height))
                Direction.D -> Point(point.x, wrapIndex((point.y + time).toLong(), height))
                Direction.L -> Point(wrapIndex((point.x - time).toLong(), width), point.y)
                Direction.R -> Point(wrapIndex((point.x + time).toLong(), width), point.y)
            }
        }
    }

    inner class Step(val pos: Point, val goal: Point, val time: Int) : GraphNode<Step> {
        val normalizedTime = time % (lcm(width, height))
        val nextBlizzards by lazy {
            getBlizzardsAtTime(time + 1)
        }

        fun Point.reachablePoints() = listOf(Point(0, 1), Point(1, 0), Point(0, -1), Point(-1, 0)).map {
            it + this
        } + this

        override val edges: List<GraphEdge<Step>>
            get() = pos.reachablePoints().filter {
                if (it == start || it == end)
                    true
                else if (it.y < 0 || it.x < 0 || it.y >= height || it.x >= width)
                    false
                else
                    !nextBlizzards.contains(it)
            }.map { GraphEdge(this, Step(it, goal, time + 1)) }

        override val optimisticCost = pos.getManhattanDistanceTo(goal)

        override val isGoal: Boolean
            get() {
                //print()
                return pos == goal
            }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Step

            if (pos != other.pos) return false
            if (normalizedTime != other.normalizedTime) return false

            return true
        }

        override fun hashCode(): Int {
            var result = pos.hashCode()
            result = 31 * result + normalizedTime
            return result
        }

        fun print() {
            println("Time: $time")
            repeat(height) { y ->
                repeat(width) { x ->
                    if (pos == Point(x, y)) {
                        print("E")
                    } else if (getBlizzardsAtTime(time).contains(Point(x, y))) {
                        print("#")
                    } else {
                        print(".")
                    }
                }
                println()
            }
            println()
        }
    }
}