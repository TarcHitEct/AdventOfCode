package aoc2021

import readInput

private fun main() {
    val list = readInput(::main).let {
        it.trim().split("\n").map { it.trim() }
    }.toList()
    println(list.filter { brokenScore(it) == 0L }.map { score(it) }.sorted().let {
        it[it.size / 2]
    })
}

fun score(line: String): Long {
    val stack = mutableListOf<Char>()
    line.forEach { bracket ->
        if (bracket in listOf('(', '[', '{', '<')) {
            stack.add(bracket)
        } else {
            stack.removeLast()
        }
    }
    return stack.reversed().fold(0L) { acc, c -> acc * 5 + validPoints[c]!! }
}

fun brokenScore(line: String): Long {
    val stack = mutableListOf<Char>()
    line.forEach { bracket ->
        if (bracket in listOf('(', '[', '{', '<')) {
            stack.add(bracket)
        } else {
            if (stack.removeLast() != open[bracket]) {
                return brokenPoints[bracket]!!.toLong()
            }
        }
    }
    return 0
}

val brokenPoints = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
)

val validPoints = mapOf(
    '(' to 1,
    '[' to 2,
    '{' to 3,
    '<' to 4
)

val open = mapOf(
    ')' to '(',
    ']' to '[',
    '}' to '{',
    '>' to '<'
)
