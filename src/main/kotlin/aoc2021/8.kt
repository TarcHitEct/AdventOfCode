package aoc2021

import readInput

private fun main() {
    val list = readInput(::main).let {
        it.trim().split("\n").map {
            val splt = it.trim().split("|")
            Pair(
                splt[0].trim().split(" ").map { it.toCharArray().toSet() },
                splt[1].trim().split(" ").map { it.toCharArray().toSet() }
            )
        }
    }.toList()
    val res = list.sumOf { line ->
        val allDigits = (line.first + line.second).toSet()
        val n1 = allDigits.find { it.size == 2 }!!
        val n4 = allDigits.find { it.size == 4 }!!
        val n7 = allDigits.find { it.size == 3 }!!
        val n8 = allDigits.find { it.size == 7 }!!
        val n3 = allDigits.find { it.size == 5 && it.containsAll(n1) }!!
        val n9 = allDigits.find { it.size == 6 && it.containsAll(n4) }!!
        val n0 = allDigits.find { it.size == 6 && it.containsAll(n1) && it != n9 }!!
        val n6 = allDigits.find { it.size == 6 && it != n9 && it != n0 }!!
        val e = n8 - n9
        val n2 = allDigits.find { it.size == 5 && it.containsAll(e) }!!
        val n5 = allDigits.find { it.size == 5 && it != n2 && it != n3 }!!

        val charMap = mapOf(
            n0 to 0, n1 to 1, n2 to 2, n3 to 3, n4 to 4, n5 to 5, n6 to 6, n7 to 7, n8 to 8, n9 to 9
        )

        charMap[line.second[0]]!! * 1000 +
                charMap[line.second[1]]!! * 100 +
                charMap[line.second[2]]!! * 10 +
                charMap[line.second[3]]!!
    }
    println(res)
}