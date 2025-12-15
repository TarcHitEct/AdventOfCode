package aoc2022

import aoc2021.Point
import readInput
import solveA
import solveB
import kotlin.math.abs

private fun main() {
    val sensors = readInput(::main).trim().lines().map {
        Regex("x=(-?\\d+), y=(-?\\d+).*x=(-?\\d+), y=(-?\\d+)").find(it)?.let {
            Sensor(
                Point(it.groupValues[1].toInt(), it.groupValues[2].toInt()),
                Point(it.groupValues[3].toInt(), it.groupValues[4].toInt())
            )
        }!!
    }

    val scanY = 2000000
    var blockedRanges = sensors.mapNotNull { sensor ->
        val yDistance = abs(sensor.position.y - scanY)
        val blockedFromCenter = sensor.beaconDistance - yDistance
        if (blockedFromCenter >= 0) {
            (sensor.position.x - blockedFromCenter..sensor.position.x + blockedFromCenter)
        } else {
            null
        }
    }.toSet()
    var lastSize: Int
    do {
        lastSize = blockedRanges.size
        blockedRanges = blockedRanges.map { range ->
            blockedRanges.filter { it.intersects(range) }.sortedBy { it.first }.reduce { a, b -> a.merge(b) }
        }.toSet()
    } while (lastSize != blockedRanges.size)
    val beacons = sensors.filter { it.closestBeaconPosition.y == scanY }
        .map { it.closestBeaconPosition.x }
        .filter { beacon -> blockedRanges.any { it.contains(beacon) } }
        .toSet().size
    blockedRanges.sumOf { it.last - it.first + 1 - beacons }.let {
        solveA(::main, it)
    }

    val searchSpace = (0..4000000)
    sensors.forEach {
        it.borderPositions().find { point ->
            if (point.x in searchSpace && point.y in searchSpace) {
                sensors.none { sensor ->
                    sensor.contains(point)
                }
            } else false
        }?.let { hiddenBeacon ->
            solveB(::main, hiddenBeacon.x * 4000000L + hiddenBeacon.y)
            return
        }
    }
}

data class Sensor(val position: Point, val closestBeaconPosition: Point) {
    val beaconDistance = position.getManhattanDistanceTo(closestBeaconPosition)

    fun borderPositions(): Sequence<Point> {
        var point = position + Point(0, -beaconDistance - 1)
        return sequence {
            repeat(beaconDistance + 1) {
                point += Point(1, 1)
                yield(point)
            }
            repeat(beaconDistance + 1) {
                point += Point(-1, 1)
                yield(point)
            }
            repeat(beaconDistance + 1) {
                point += Point(-1, -1)
                yield(point)
            }
            repeat(beaconDistance + 1) {
                point += Point(1, -1)
                yield(point)
            }
        }
    }

    fun contains(point: Point) = position.getManhattanDistanceTo(point) <= beaconDistance
}

private fun IntRange.intersects(other: IntRange): Boolean {
    return this.contains(other.first) || this.contains(other.last) || other.contains(this.first) || other.contains(this.last)
}

private fun IntRange.merge(other: IntRange): IntRange {
    if (!this.intersects(other)) {
        throw IllegalArgumentException("cannot merge")
    }
    return IntRange(kotlin.math.min(first, other.first), kotlin.math.max(last, other.last))
}