package aoc2020

import readInput

private fun main() {
    var list = readInput(::main).let {
        it.trim().split("\n").map { it.trim().toLong() }
    }.toList()
    val invalidIdx = (25 until list.size).find {
        (it - 25 until it).none { a ->
            (a + 1 until it).any { b ->
                list[b] + list[a] == list[it]
            }
        }
    }!!
    println(list[invalidIdx])

    (0..invalidIdx).forEach { a ->
        (a + 1..invalidIdx).forEach { b ->
            val cSet = list.subList(a, b)
            if (cSet.sum() == list[invalidIdx]) {
                println(cSet.minOrNull()!! + cSet.maxOrNull()!!)
                return
            }
        }
    }
}