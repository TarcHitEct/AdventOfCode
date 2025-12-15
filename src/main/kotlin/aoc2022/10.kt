package aoc2022

import readInput
import solveA
import solveB

private fun main() {
    val instructions = readInput(::main).let {
        Regex("(addx|noop) ?((-|\\d)*)").findAll(it).map {
            Instruction10(
                InstructionType.valueOf(it.groupValues[1].uppercase()),
                it.groupValues[2].uppercase().ifBlank { "0" }.toInt()
            )
        }
    }.toList()

    val cpu = CPU()
    instructions.forEach { cpu.execute(it) }
    (20..220 step 40).sumOf { cpu.cycleValues[it - 1] * it }.let {
        solveA(::main, it)
    }

    (1..240).forEach { cycle ->
        val cycleX = cpu.cycleValues[cycle - 1]
        val spriteSpan = (cycleX - 1..cycleX + 1)
        val drawingPos = (cycle - 1) % 40
        if(spriteSpan.contains(drawingPos)) print("#") else print(".")
        if (cycle % 40 == 0) println()
    }
    solveB(::main, "ZGCJZJFL")
}

private class CPU {
    val cycleValues = mutableListOf(1)

    fun execute(instr: Instruction10) {
        when (instr.type) {
            InstructionType.NOOP -> cycleValues.add(getX())
            InstructionType.ADDX -> {
                cycleValues.add(getX())
                cycleValues.add(getX() + instr.param)
            }
        }
    }

    fun getX() = cycleValues.last()
}

data class Instruction10(val type: InstructionType, val param: Int)

enum class InstructionType { ADDX, NOOP }
