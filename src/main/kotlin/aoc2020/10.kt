package aoc2020

import readInput

private fun main() {
    val list = readInput(::main).let {
        it.trim().split("\n").map { it.trim().toInt() }
    }.toList().let { it + (it.maxOrNull()!! + 3) + 0 }.sorted()
    val diff = (1 until list.size).map {
        list[it] - list[it - 1]
    }
    val counts = diff.groupBy { it }.mapValues { it.value.size }
    println(list)
    println(diff)
    println(counts)
    println(counts[1]!! * counts[3]!!)

    diff.joinToString("").let {
        Regex("1+").findAll(it).map { it.value.length }
    }.map {
        when (it) {
            1 -> 1L
            2 -> 2L
            3 -> 4L
            4 -> 7L
            else -> throw Exception("unsupported $it")
        }
    }.reduce { acc, i -> acc * i }.also {
        println(it)
    }
}