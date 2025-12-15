package aoc2020

import aoc2020.Vector.Companion.East
import aoc2020.Vector.Companion.North
import aoc2020.Vector.Companion.South
import aoc2020.Vector.Companion.West
import readInput
import kotlin.math.absoluteValue

private fun main() {
    val list = readInput(::main).let {
        it.trim().split("\n").map {
            Instruction(it.trim()[0], it.trim().substring(1).toInt())
        }
    }.toList()

    var position = Vector(0, 0)
    var waypoint = East * 10 + North
    list.forEach {
        when (it.type) {
            'N' -> waypoint += North * it.value
            'E' -> waypoint += East * it.value
            'S' -> waypoint += South * it.value
            'W' -> waypoint += West * it.value
            'L' -> waypoint = waypoint.rotateLeft(it.value)
            'R' -> waypoint = waypoint.rotateRight(it.value)
            'F' -> position += waypoint * it.value
            else -> throw Exception("unsupported ${it.type}")
        }
    }
    println(position.x.absoluteValue + position.y.absoluteValue)
}

private data class Instruction(val type: Char, val value: Int)

class Vector(val x: Int, val y: Int) {

    companion object {
        val North = Vector(0, 1)
        val East = Vector(1, 0)
        val South = Vector(0, -1)
        val West = Vector(-1, 0)
    }

    operator fun plus(v2: Vector): Vector {
        return Vector(x + v2.x, y + v2.y)
    }

    operator fun times(f: Int): Vector {
        return Vector(x * f, y * f)
    }

    fun rotateLeft(degree: Int): Vector {
        return when (degree) {
            90 -> Vector(-y, x)
            180 -> Vector(-x, -y)
            270 -> Vector(y, -x)
            else -> throw Exception("unsupported $degree")
        }
    }

    fun rotateRight(degree: Int): Vector {
        return rotateLeft(360 - degree)
    }
}