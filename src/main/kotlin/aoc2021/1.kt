package aoc2021

import readInput

private fun main() {
    val list = readInput(::main).let {
        it.split("\n").filter { it.isNotBlank() }.map {
            it.toString().trim().toInt()
        }
    }.toList().windowed(3)
    println(list.withIndex().count {
        it.index != 0 && it.value.sum() > list.get(it.index - 1).sum()
    })
}