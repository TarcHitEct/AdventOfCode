package aoc2022

import readInput
import solveA
import solveB

private fun main() {
    val riddle = MonkeyRiddle()
    readInput(::main).let {
        Regex("(....): ((\\d+)|((....) (.) (....)))").findAll(it).forEach {
            if (it.groupValues[3].isBlank()) {
                riddle.addMonkey(it.groupValues[1], it.groupValues[5], it.groupValues[6], it.groupValues[7])
            } else {
                riddle.addMonkey(it.groupValues[1], it.groupValues[3].toLong())
            }
        }
    }
    solveA(::main, riddle.monkeys.single { it.name == "root" }.number)
    solveB(::main, riddle.calcHumn())
}

interface IMonkey {
    val name: String
    val number: Long

    fun containsHumn(): Boolean

    fun setExpected(n: Long): Long
}

class MonkeyRiddle {

    val monkeys = mutableListOf<IMonkey>()

    fun addMonkey(name: String, number: Long) {
        monkeys.add(MonkeyNumber(name, number))
    }

    fun addMonkey(name: String, operandAMonkey: String, operator: String, operandBMonkey: String) {
        monkeys.add(MonkeyOperation(name, operandAMonkey, operator, operandBMonkey))
    }

    fun calcHumn(): Long {
        val root = monkeys.single { it.name == "root" } as MonkeyOperation
        if (root.operandAMonkey.containsHumn()) {
            return root.operandAMonkey.setExpected(root.operandBMonkey.number)
        }
        if (root.operandBMonkey.containsHumn()) {
            return root.operandBMonkey.setExpected(root.operandAMonkey.number)
        }
        throw IllegalStateException("Humn not found")
    }

    inner class MonkeyNumber(override val name: String, override val number: Long) : IMonkey {
        override fun containsHumn() = name == "humn"

        override fun setExpected(n: Long): Long {
            return n
        }
    }

    inner class MonkeyOperation(
        override val name: String,
        val operandAMonkeyName: String,
        val operator: String,
        val operandBMonkeyName: String
    ) : IMonkey {
        val operandAMonkey by lazy { monkeys.single { it.name == operandAMonkeyName } }
        val operandBMonkey by lazy { monkeys.single { it.name == operandBMonkeyName } }
        override val number by lazy {
            when (operator) {
                "+" -> operandAMonkey.number + operandBMonkey.number
                "-" -> operandAMonkey.number - operandBMonkey.number
                "*" -> operandAMonkey.number * operandBMonkey.number
                "/" -> operandAMonkey.number / operandBMonkey.number
                else -> throw IllegalStateException("Unknown operator $operator")
            }
        }

        override fun containsHumn(): Boolean {
            return operandAMonkey.containsHumn() || operandBMonkey.containsHumn()
        }

        override fun setExpected(n: Long): Long {
            if (operandAMonkey.containsHumn()) {
                val expected = when (operator) {
                    "+" -> n - operandBMonkey.number
                    "-" -> n + operandBMonkey.number
                    "*" -> n / operandBMonkey.number
                    "/" -> n * operandBMonkey.number
                    else -> throw IllegalStateException("Unknown operator $operator")
                }
                return operandAMonkey.setExpected(expected)
            }
            if (operandBMonkey.containsHumn()) {
                val expected = when (operator) {
                    "+" -> n - operandAMonkey.number
                    "-" -> operandAMonkey.number - n
                    "*" -> n / operandAMonkey.number
                    "/" -> operandAMonkey.number / n
                    else -> throw IllegalStateException("Unknown operator $operator")
                }
                return operandBMonkey.setExpected(expected)
            }
            throw IllegalStateException("Humn not found")
        }
    }
}
