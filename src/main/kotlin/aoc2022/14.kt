package aoc2022

import GraphEdge
import GraphNode
import TraverseResult
import aoc2021.Point
import readInput
import solveA
import solveB
import traverse
import kotlin.math.max
import kotlin.math.min

private fun main() {
    val paths = readInput(::main).trim().lines().map {
        Regex("(\\d+),(\\d+)").findAll(it).map {
            Point(it.groupValues[1].toInt(), it.groupValues[2].toInt())
        }.toList()
    }
    val caveA = Cave(paths)
    caveA.fillWithSand(true)
    solveA(::main, caveA.countGrains())
    val caveB = Cave(paths)
    caveB.fillWithSand(false)
    solveB(::main, caveB.countGrains())
}

class Cave(paths: List<List<Point>>) {
    val source = Point(500, 0)
    val cells = mutableMapOf(source to CellType.Source)
    val endlessVoidY: Int
    val floorY: Int

    init {
        paths.forEach { path ->
            path.windowed(2).forEach { (from, to) ->
                val fromX = min(from.x, to.x)
                val fromY = min(from.y, to.y)
                val toX = max(from.x, to.x)
                val toY = max(from.y, to.y)
                (fromX..toX).forEach { x ->
                    (fromY..toY).forEach { y ->
                        cells[Point(x, y)] = CellType.Rock
                    }
                }
            }
        }
        endlessVoidY = cells.keys.maxOf { it.y }
        floorY = cells.keys.maxOf { it.y + 2 }
    }

    fun fillWithSand(withEndlessVoid: Boolean): TraverseResult<FallingGrain> {
        return FallingGrain(source).traverse { path, traverseResult, returning ->
            val fallingGrain = path.last()
            if (returning && fallingGrain.edges.isEmpty()) {
                fallingGrain.deposit()
            }
            if (withEndlessVoid && fallingGrain.position.y >= endlessVoidY) {
                traverseResult.done = true
            }
        }
    }

    fun countGrains() = cells.values.count { it == CellType.Sand }

    inner class FallingGrain(val position: Point) : GraphNode<FallingGrain> {
        override val edges
            get() = listOf(
                position + Point(0, 1),
                position + Point(-1, 1),
                position + Point(1, 1)
            ).filter {
                cells[it] == null && it.y < floorY
            }.map {
                GraphEdge(this, FallingGrain(it))
            }

        fun deposit() {
            cells[position] = CellType.Sand
        }
    }
}

enum class CellType { Source, Rock, Sand }