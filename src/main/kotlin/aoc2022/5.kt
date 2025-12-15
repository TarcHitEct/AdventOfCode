package aoc2022

import readInput
import solveB
import java.util.*

private fun main() {
    val stacks = Stacks();
    readInput(::main).let {
        it.lines().forEach {
            Regex("\\[(.)\\]").findAll(it).forEach {
                val stackNr = it!!.range.first / 4 + 1
                stacks.put(stackNr, it.groupValues[1])
            }
        }
    }
    val instructions = readInput(::main).let {
        Regex("move (\\d*) from (\\d*) to (\\d*)").findAll(it).map {
            Instruction(it.groupValues[1].toInt(), it.groupValues[2].toInt(), it.groupValues[3].toInt())
        }
    }.toList()

    //instructions.forEach { stacks.execute(it) }
    //solveA(::main, stacks.topCrates().joinToString(""))
    instructions.forEach { stacks.execute2(it) }
    solveB(::main, stacks.topCrates().joinToString(""))
}

private data class Instruction(val amount: Int, val from: Int, val to: Int)

private class Stacks {
    private val stacks = TreeMap<Int, MutableList<String>>()
    fun put(stackNr: Int, crate: String) {
        stacks.computeIfAbsent(stackNr) { mutableListOf() }.add(crate)
    }

    fun execute(instruction: Instruction) {
        repeat(instruction.amount) {
            stacks[instruction.to]!!.add(0, stacks[instruction.from]!!.removeFirst())
        }
    }

    fun execute2(instruction: Instruction) {
        val fromStack = stacks[instruction.from]!!
        val toStack = stacks[instruction.to]!!
        val moveCrates = fromStack.take(instruction.amount);
        repeat(instruction.amount) {
            fromStack.removeFirst()
        }
        toStack.addAll(0, moveCrates)
    }

    fun topCrates(): List<String> {
        return stacks.values.map { it.first() }
    }
}