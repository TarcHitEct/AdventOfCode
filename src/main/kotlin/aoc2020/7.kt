package aoc2020

import readInput

private fun main() {
    var list = readInput(::main).let {
        Regex("([^ ]* [^ ]*) bags contain ([^.]*).").findAll(it).map {
            val outerBag = it.groupValues[1].trim()
            val innerStr = it.groupValues[2].trim()
            val innerBags: Map<String, Int> = if (innerStr == "no other bags") {
                emptyMap()
            } else {
                Regex("(\\d+) ([^ ]* [^ ]*) bag").findAll(innerStr).map {
                    val count = it.groupValues[1].toInt()
                    val innerBag = it.groupValues[2].trim()
                    innerBag to count
                }.toMap()
            }
            Bag(outerBag, innerBags)
        }
    }
    /* // solution to A:
    val resultBags = mutableSetOf("shiny gold")
    do {
        val newBags = list.filter { bag -> bag.subBags.keys.any { resultBags.contains(it) } }
    } while (resultBags.addAll(newBags.map { it.name }))
    println(resultBags.size - 1)*/

    // solution to B:
    println(countSubBags("shiny gold", list.toList()))
}

fun countSubBags(bagStr: String, allBags: Collection<Bag>): Int {
    val bag = allBags.find { it.name == bagStr }!!
    return bag.subBags.entries.sumOf {
        it.value * (countSubBags(it.key, allBags) + 1)
    }
}

data class Bag(val name: String, val subBags: Map<String, Int>)