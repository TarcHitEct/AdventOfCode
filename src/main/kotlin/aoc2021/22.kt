package aoc2021

import readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private fun main() {
    val instructions = readInput(::main).let {
        it.split("\n").map {
            Regex("(.*) x=(.*),y=(.*),z=(.*)").find(it)?.let {
                Instruction(
                    it.groupValues[1] == "on", Cuboid(
                        it.groupValues[2].split(".").first().toLong(),
                        it.groupValues[2].split(".").last().toLong(),
                        it.groupValues[3].split(".").first().toLong(),
                        it.groupValues[3].split(".").last().toLong(),
                        it.groupValues[4].split(".").first().toLong(),
                        it.groupValues[4].split(".").last().toLong()
                    )
                )
            }!!
        }
    }

    val smallInstructionSet = splitInstructions(instructions.filter {
        abs(it.cuboid.fromX) <= 50 &&
                abs(it.cuboid.toX) <= 50 &&
                abs(it.cuboid.fromY) <= 50 &&
                abs(it.cuboid.toY) <= 50 &&
                abs(it.cuboid.fromZ) <= 50 &&
                abs(it.cuboid.toZ) <= 50
    })
    test(smallInstructionSet)
    val state = mutableMapOf<Cuboid, Boolean>()
    smallInstructionSet.forEach {
        state[it.cuboid] = it.on
    }
    println(state.entries.filter { it.value }.sumOf { it.key.size() })

    val fullInstructionSet = splitInstructions(instructions)
    val fullState = mutableMapOf<Cuboid, Boolean>()
    fullInstructionSet.forEach {
        fullState[it.cuboid] = it.on
    }
    println(fullState.entries.filter { it.value }.sumOf { it.key.size() })
}

private fun test(smallInstructionSet: List<Instruction>) {
    val cubes = smallInstructionSet.map { it.cuboid }.distinct()
    cubes.forEachIndexed { i, a ->
        (i + 1 until cubes.size).forEach { bI ->
            val b = cubes[bI]
            val int = a.intersect(b)
            if (int != null) {
                throw Exception("Overlapping instructions")
            }
        }
    }
}

private fun splitInstructions(instructions: List<Instruction>): List<Instruction> {
    Cuboid(
        instructions.minOf { it.cuboid.fromX },
        instructions.maxOf { it.cuboid.toX },
        instructions.minOf { it.cuboid.fromY },
        instructions.maxOf { it.cuboid.toY },
        instructions.minOf { it.cuboid.fromZ },
        instructions.maxOf { it.cuboid.toZ },
    ).size().let {
        println(it)
    }
    cubes = mutableSetOf()
    return instructions.flatMap { a ->
        val splitPoints = mutableSetOf<Position>()
        instructions.forEach { b ->
            val int = a.cuboid.intersect(b.cuboid)
            if (int != null) {
                splitPoints.addAll(int.getCorners())
            }
        }
        cubes.forEach { b ->
            val int = a.cuboid.intersect(b)
            if (int != null) {
                splitPoints.addAll(int.getCorners())
            }
        }
        a.splitBy(splitPoints)
    }
}

var cubes = mutableSetOf<Cuboid>()

data class Cuboid(
    val fromX: Long, val toX: Long,
    val fromY: Long, val toY: Long,
    val fromZ: Long, val toZ: Long,
) {
    fun size(): Long {
        return (abs(fromX - toX) + 1) * (abs(fromY - toY) + 1) * (abs(fromZ - toZ) + 1)
    }

    fun getCorners(): List<Position> {
        return listOf(
            Position(fromX, fromY, fromZ),
            Position(toX, fromY, fromZ),
            Position(fromX, toY, fromZ),
            Position(fromX, fromY, toZ),
            Position(toX, toY, fromZ),
            Position(fromX, toY, toZ),
            Position(toX, fromY, toZ),
            Position(toX, toY, toZ),
        )
    }

    fun contains(point: Position): Boolean {
        return point.x in fromX..toX &&
                point.y in fromY..toY &&
                point.z in fromZ..toZ
    }

    fun intersect(other: Cuboid): Cuboid? {
        val newFromX = max(fromX, other.fromX)
        val newToX = min(toX, other.toX)
        val newFromY = max(fromY, other.fromY)
        val newToY = min(toY, other.toY)
        val newFromZ = max(fromZ, other.fromZ)
        val newToZ = min(toZ, other.toZ)
        if (newFromX <= newToX && newFromY <= newToY && newFromZ <= newToZ) {
            return Cuboid(newFromX, newToX, newFromY, newToY, newFromZ, newToZ)
        }
        return null
    }

    fun splitBy(splitPoints: Set<Position>): Set<Cuboid> {
        if (splitPoints.isEmpty()) {
            return setOf(this)
        }
        val cubes = cubes.filter { it.intersect(this) != null }.toMutableSet()
        val xSplits = splitPoints.map { it.x }.distinct().let {
            (it + it).filter {
                it in (fromX..toX)
            }.sorted()
        }
        val ySplits = splitPoints.map { it.y }.distinct().let {
            (it + it).filter {
                it in (fromY..toY)
            }.sorted()
        }
        val zSplits = splitPoints.map { it.z }.distinct().let {
            (it + it).filter {
                it in (fromZ..toZ)
            }.sorted()
        }
        return xSplits.windowed(2).flatMap { x ->
            ySplits.windowed(2).flatMap { y ->
                zSplits.windowed(2).mapNotNull { z ->
                    var xFrom = x[0]
                    var xTo = x[1]
                    var yFrom = y[0]
                    var yTo = y[1]
                    var zFrom = z[0]
                    var zTo = z[1]
                    if (xFrom != xTo) {
                        xFrom++
                        xTo--
                    }
                    if (yFrom != yTo) {
                        yFrom++
                        yTo--
                    }
                    if (zFrom != zTo) {
                        zFrom++
                        zTo--
                    }
                    if (xFrom <= xTo && yFrom <= yTo && zFrom <= zTo) {
                        val subCube = Cuboid(xFrom, xTo, yFrom, yTo, zFrom, zTo)
                        if (subCube.getCorners().all { this.contains(it) }) {
                            val existingCube = cubes.find { it.intersect(subCube) != null }
                            if (existingCube == null) {
                                cubes.add(subCube)
                                return@mapNotNull subCube
                            } else {
                                return@mapNotNull existingCube
                            }
                        }
                    }
                    null
                }
            }
        }.toSet()
    }
}

private data class Instruction(val on: Boolean, val cuboid: Cuboid) {
    fun splitBy(splitPoints: Set<Position>): List<Instruction> {
        return cuboid.splitBy(splitPoints).also {
            /*if (!it.all { cuboid.intersect(it) == it }) {
                throw Exception("outside")
            }
            if (cuboid.size() != it.sumOf { it.size() }) {
                throw Exception("split invalid")
            }*/
        }.map {
            Instruction(on, it)
        }.also {
            //test(it)
        }
    }
}