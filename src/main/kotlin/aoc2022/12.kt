package aoc2022

import GraphEdge
import GraphNode
import dijkstra
import readInput
import solveA
import solveB

private fun main() {
    val terrainHeights = readInput(::main).trim().lines().map { it.toCharArray() }
    val terrain = Terrain(terrainHeights)
    solveA(::main, terrain.start.distanceFromEnd!!)
    solveB(::main, terrain.cells.flatten().filter { it.height == 0 }.mapNotNull { it.distanceFromEnd }.min())
}

class Terrain(terrainHeights: List<CharArray>) {
    val cells = terrainHeights.mapIndexed { x, row -> row.mapIndexed { y, char -> TerrainCell(x, y, char) } }
    val start = cells.flatten().single { it.isStart }
    val end = cells.flatten().single { it.isEnd }
    val dijkstraResult by lazy {
        end.dijkstra()
    }

    inner class TerrainCell(val x: Int, val y: Int, val char: Char) : GraphNode<TerrainCell> {
        val isStart = char == 'S'
        val isEnd = char == 'E'
        val height = char.let {
            val heightChar = if (isStart) 'a' else if (isEnd) 'z' else char
            heightChar - 'a'
        }
        val neighbourCells by lazy {
            listOfNotNull(
                cells.getOrNull(x - 1)?.getOrNull(y), cells.getOrNull(x + 1)?.getOrNull(y),
                cells.getOrNull(x)?.getOrNull(y - 1), cells.getOrNull(x)?.getOrNull(y + 1)
            )
        }
        val reachableCells by lazy {
            neighbourCells.filter { neighbour -> neighbour.isCellReachable(this) }
        }
        val distanceFromEnd by lazy {
            dijkstraResult.distances[this]
        }
        override val edges by lazy {
            reachableCells.map { GraphEdge(this, it) }
        }

        fun isCellReachable(other: TerrainCell) = other.height <= height + 1
    }
}