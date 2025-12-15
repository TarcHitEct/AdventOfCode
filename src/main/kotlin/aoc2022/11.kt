package aoc2022

import readInput
import solveA
import solveB
import java.util.function.Function

private fun main() {
    val monkeysOriginal = readInput(::main).let {
        Regex(
            "Monkey \\d:\n" +
                    ".*Starting items: (.*)\n" +
                    ".*Operation: new = old (.) (.*)\n" +
                    ".*Test: divisible by (.*)\n" +
                    ".*throw to monkey (.*)\n" +
                    ".*throw to monkey (.*)"
        ).findAll(it).map {
            Monkey(
                it.groupValues[1].split(",").map { it.trim().toLong() }.toMutableList(),
                { old ->
                    val operator = it.groupValues[2]
                    val operandStr = it.groupValues[3]
                    val operand = if (operandStr == "old") old else operandStr.toLong()
                    when (operator) {
                        "+" -> old + operand
                        "*" -> old * operand
                        else -> throw IllegalArgumentException("Invalid operator $operator")
                    }
                },
                it.groupValues[4].toLong(),
                it.groupValues[5].toInt(),
                it.groupValues[6].toInt()
            )
        }
    }.toList()

    var monkeys = monkeysOriginal.map { it.clone() }
    repeat(20) { simulateRound(monkeys, 3) }
    monkeys.map { it.inspectionCount }.sortedDescending().let {
        solveA(::main, it[0] * it[1])
    }

    monkeys = monkeysOriginal.map { it.clone() }
    val normalize = monkeys.map { it.divTest }.reduce { acc, n -> acc * n }
    repeat(10000) { simulateRound(monkeys, 1, normalize) }
    monkeys.map { it.inspectionCount }.sortedDescending().let {
        solveB(::main, it[0] * it[1])
    }
}

private fun simulateRound(monkeys: List<Monkey>, worryDecay: Long, normalize: Long? = null) {
    monkeys.forEach { monkey ->
        while (monkey.items.isNotEmpty()) {
            monkey.inspectionCount++
            var item = monkey.items.removeFirst()
            item = (monkey.operation.apply(item) / worryDecay)
            if (normalize != null) {
                item %= normalize
            }
            if (item % monkey.divTest == 0L) {
                monkeys[monkey.trueMonkey].items += item
            } else {
                monkeys[monkey.falseMonkey].items += item
            }
        }
    }
}

data class Monkey(
    val items: MutableList<Long>,
    val operation: Function<Long, Long>,
    val divTest: Long,
    val trueMonkey: Int,
    val falseMonkey: Int,
    var inspectionCount: Long = 0
) {
    fun clone() = copy(items = items.toMutableList())
}