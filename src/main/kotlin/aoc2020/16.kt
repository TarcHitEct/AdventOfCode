package aoc2020

import readInput

private fun main() {
    val input = readInput(::main)
    val rules = Regex("([^:]*): (\\d*)-(\\d*) or (\\d*)-(\\d*)").findAll(input).map {
        Rule(
            it.groupValues[1].trim(),
            (it.groupValues[2].toInt()..it.groupValues[3].toInt()),
            (it.groupValues[4].toInt()..it.groupValues[5].toInt())
        )
    }.toList()
    val nearby = input.let {
        val start = it.indexOf("nearby tickets:") + "nearby tickets:".length
        it.substring(start).trim().split("\n").map {
            Ticket(it.trim().split(",").map { it.trim().toInt() })
        }
    }
    val valid = nearby.filter { ticket ->
        ticket.values.all { value ->
            rules.any { rule ->
                value in rule.range1 || value in rule.range2
            }
        }
    }
    (nearby - valid).flatMap { it.values }.filter { value ->
        rules.none { rule ->
            value in rule.range1 || value in rule.range2
        }
    }.sum().also {
        println(it)
    }

    val myTicket = input.let {
        val start = it.indexOf("your ticket:") + "your ticket:".length
        it.substring(start).trim().split("\n").first().let {
            Ticket(it.trim().split(",").map { it.trim().toInt() })
        }
    }
    while (rules.any { it.index == null }) {
        val openIndices = valid.first().values.indices.toList() - rules.mapNotNull { it.index }
        val openRules = rules.filter { it.index == null }
        for (idx in openIndices) {
            val matchingRules = openRules.filter { rule ->
                valid.all { it.values[idx] in rule.range1 || it.values[idx] in rule.range2 }
            }
            if (matchingRules.size == 1) {
                matchingRules.first().index = idx
                continue
            }
        }
    }
    rules.filter { it.fieldName.startsWith("departure") }.map {
        myTicket.values[it.index!!].toLong()
    }.reduce { acc, i -> acc * i }.also {
        println(it)
    }
}

data class Rule(val fieldName: String, val range1: IntRange, val range2: IntRange, var index: Int? = null)
data class Ticket(val values: List<Int>)