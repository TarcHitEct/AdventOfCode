package aoc2021

import readInput

private fun main() {
    val list = readInput(::main).let {
        it.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }.toList()
    val n = list.first().length
    val a = (0 until n).fold(list) { acc, i ->
        filter1(acc, i)
    }.first().toInt(2)
    val b = (0 until n).fold(list) { acc, i ->
        filter2(acc, i)
    }.first().toInt(2)
    println(a * b)
}

fun filter1(numbers: List<String>, i: Int): List<String> {
    if (numbers.size == 1) {
        return numbers
    }
    val th = numbers.size.toDouble() / 2
    val c = numbers.count { it[i].toString() == "1" }
    if (c >= th) {
        return numbers.filter { it[i].toString() == "1" }
    } else {
        return numbers.filter { it[i].toString() != "1" }
    }
}

fun filter2(numbers: List<String>, i: Int): List<String> {
    if (numbers.size == 1) {
        return numbers
    }
    val th = numbers.size.toDouble() / 2
    val c = numbers.count { it[i].toString() == "0" }
    if (c <= th) {
        return numbers.filter { it[i].toString() == "0" }
    } else {
        return numbers.filter { it[i].toString() != "0" }
    }
}