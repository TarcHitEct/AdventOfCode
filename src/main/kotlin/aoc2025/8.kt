package aoc2025

import aoc2022.Point3D
import readInput
import solveA
import solveB

private fun main() {
    val points = readInput(::main).trim().split("\n").map { line ->
        line.split(",").let {
            Point3D(it[0].toInt(), it[1].toInt(), it[2].toInt())
        }
    }
    val rigging = Rigging(points)
    repeat(1000) {
        val toConnect = rigging.boxes.minBy { it.distanceToClosest() }
        toConnect.connectToClosest()
    }
    val resultA = rigging.boxes
        .groupBy { it.circuit }.values
        .map { it.size.toLong() }
        .sortedDescending()
        .take(3)
        .reduce { a, b -> a * b }

    solveA(::main, resultA)

    var lastPair: Pair<Rigging.Box, Rigging.Box>? = null
    do {
        val toConnect = rigging.boxes.minBy { it.distanceToClosest() }
        lastPair = Pair(toConnect, toConnect.closest())
        toConnect.connectToClosest()
    } while (rigging.boxes.groupBy { it.circuit }.size > 1)
    solveB(::main, lastPair.first.pos.x * lastPair.second.pos.x)
}

private class Rigging(points: List<Point3D>) {
    val boxes = points.mapIndexed { i, p -> Box(p, i) }

    inner class Box(val pos: Point3D, var circuit: Int) {
        private var closest: Box? = null
        private val connections = mutableListOf<Box>()

        fun closest(): Box {
            if (closest == null) {
                closest = boxes.minBy { other ->
                    if (this == other || other in this.connections) Double.MAX_VALUE else this.distanceTo(other)
                }
                assert(closest !in this.connections)
            }
            return closest!!
        }

        fun distanceToClosest(): Double {
            return distanceTo(closest())
        }

        fun distanceTo(other: Box): Double {
            return pos.distanceTo(other.pos)
        }

        fun connectToClosest() {
            connectTo(closest())
        }

        fun connectTo(other: Box) {
            val replaceCircuit = other.circuit
            boxes.forEach { if (it.circuit == replaceCircuit) it.circuit = this.circuit }
            this.connections.add(other)
            other.connections.add(this)
            other.closest = null
            closest = null
        }
    }
}
