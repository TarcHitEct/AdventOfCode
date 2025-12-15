package aoc2022

import com.fasterxml.jackson.databind.ObjectMapper
import readInput
import solveA
import solveB
import kotlin.math.max

private fun main() {
    val json = ObjectMapper()
    val pairs = readInput(::main).trim().split(Regex("\n\n")).map {
        val (left, right) = it.split("\n")
        json.readValue(left, List::class.java) to json.readValue(right, List::class.java)
    }

    pairs.mapIndexed { index, pair ->
        if (compare(pair.first, pair.second) < 0) index + 1 else 0
    }.sum().let {
        solveA(::main, it)
    }

    val marker1 = listOf(listOf(2))
    val marker2 = listOf(listOf(6))
    (pairs.flatMap { it.toList() } + listOf(marker1, marker2)).sortedWith(::compare).let {
        val idx1 = it.indexOf(marker1) + 1
        val idx2 = it.indexOf(marker2) + 1
        solveB(::main, idx1 * idx2)
    }
}

private fun compare(left: Any?, right: Any?): Int {
    if (left == null && right == null) {
        return 0
    } else if (left == null) {
        return -1
    } else if (right == null) {
        return 1
    } else if (left is Int && right is Int) {
        return left - right
    } else if (left is List<*> && right is List<*>) {
        (0 until max(left.size, right.size)).forEach { i ->
            compare(left.getOrNull(i), right.getOrNull(i)).also {
                if (it != 0) {
                    return it
                }
            }
        }
    } else {
        val leftList = if (left is List<*>) left else listOf(left)
        val rightList = if (right is List<*>) right else listOf(right)
        return compare(leftList, rightList)
    }
    return 0
}