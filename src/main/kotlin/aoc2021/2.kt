package aoc2021

import readInput

private fun main() {
    val list = readInput(::main).let {
        Regex("(.*) (.*)").findAll(it).map {
            Pair(it.groupValues[1], it.groupValues[2].toInt())
        }
    }.toList()
    var x = 0
    var y = 0
    var aim = 0
    list.forEach {
        when (it.first) {
            "forward" -> {
                x += it.second
                y += it.second * aim
            }
            "down" -> aim += it.second
            "up" -> aim -= it.second
            else -> throw Exception()
        }
    }
    println(x * y)
}