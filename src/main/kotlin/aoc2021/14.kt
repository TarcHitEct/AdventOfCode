package aoc2021

import readInput

private fun main() {
    val input = readInput(::main)
    val template = input.split("\n")[0].trim()
    val insertions = input.let {
        Regex("(..) -> (.)").findAll(it).map {
            Insertion(it.groupValues[1], it.groupValues[2])
        }
    }.toList()

    val charCounts = template.toCharArray().groupBy { it }.mapValues { it.value.size.toLong() }.toMutableMap()
    var pairCounts = template.windowed(2).groupBy { it }.mapValues { it.value.size.toLong() }

    repeat(40) {
        val newPairCounts = mutableMapOf<String, Long>()
        pairCounts.forEach { (pair, count) ->
            val insertion = insertions.find { it.match == pair }
            if (insertion != null) {
                charCounts.compute(insertion.insert[0]) { _, oldCount ->
                    (oldCount ?: 0L) + count
                }
                val p1 = pair[0] + insertion.insert
                val p2 = insertion.insert + pair[1]
                newPairCounts.compute(p1) { _, oldCount ->
                    (oldCount ?: 0L) + count
                }
                newPairCounts.compute(p2) { _, oldCount ->
                    (oldCount ?: 0L) + count
                }
            } else {
                newPairCounts.compute(pair) { _, oldCount ->
                    (oldCount ?: 0L) + count
                }
            }
        }
        pairCounts = newPairCounts
    }

    println(charCounts.values.maxOrNull()!! - charCounts.values.minOrNull()!!)
}

data class Insertion(val match: String, val insert: String)