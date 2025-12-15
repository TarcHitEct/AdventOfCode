package aoc2025

import readInput
import solveA
import solveB
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

private fun main() {
    val machines = readInput(::main).trim().split("\n").map { line ->
        val targetLeds = line.split("[", "]")[1]
            .replace(".", "0")
            .replace("#", "1")
        val buttons = Regex("""\(((\d+,?)+)\)""").findAll(line).map { button ->
            button.groupValues[1].split(",").map { it.toInt() }
        }.toList()
        val joltages = line.split("{", "}")[1]
            .split(",")
            .map { it.toInt() }
        Machine(targetLeds, buttons, joltages)
    }

    val resultA = machines.sumOf { machine ->
        (0..<(1 shl machine.buttons.size)).minOf { activeButtonsBits ->
            var activeLeds = 0
            var nrActive = 0
            machine.buttonLedsBits.forEachIndexed { i, ledBits ->
                if ((activeButtonsBits shr i) % 2 == 1) {
                    activeLeds = activeLeds xor ledBits
                    nrActive++
                }
            }
            if (activeLeds == machine.targetLedsBits)
                nrActive
            else
                Int.MAX_VALUE
        }
    }
    solveA(::main, resultA)

    val done = AtomicInteger()
    val resultB = machines.parallelStream().mapToInt { machine ->
        solveRec(machine.joltages, optimizeOrder(machine.buttons, machine.joltages), 0).also {
            println(">>>>>>>>>>>>>>>>>>>> ${done.incrementAndGet()}/${machines.size}: $it $machine")

        }
    }.sum()
    solveB(::main, resultB)
}

private fun optimizeOrder(buttons: List<List<Int>>, targetJoltages: List<Int>): List<List<Int>> {
    var best = buttons.sortedBy { a ->
        buttons.sumOf { b ->
            if (a == b) 0 else a.intersect(b).size
        }
    }.sortedByDescending {
        it.size
    }.sortedBy { a ->
        a.minOf { idx ->
            targetJoltages[idx]
        }
    }

    return best
}

private fun solveRec(targetJoltages: List<Int>, buttons: List<List<Int>>, nPressesBefore: Int): Int {
    if (buttons.isEmpty()) {
        return nPressesBefore
    }
    val pressing = buttons.first()
    val remainingButtons = buttons.drop(1)
    val range = pressing.map { idx ->
        val lastChance = remainingButtons.none { it.any { it == idx } }
        if (lastChance)
            targetJoltages[idx]..targetJoltages[idx]
        else
            0..targetJoltages[idx]
    }.reduce { a, b -> a.overlap(b) }
    return range.minOfOrNull { nPresses ->
        val newTarget = targetJoltages.toMutableList()
        pressing.forEach { newTarget[it] -= nPresses }
        solveRec(newTarget, remainingButtons, nPressesBefore + nPresses)
    } ?: Int.MAX_VALUE
}

private fun IntRange.overlap(other: IntRange): IntRange {
    val (a, b) = if (this.first <= other.first) Pair(this, other) else Pair(other, this)
    if (b.first > a.last) return IntRange.EMPTY
    return b.first..min(b.last, a.last)
}

private data class Machine(val targetLeds: String, val buttons: List<List<Int>>, val joltages: List<Int>) {
    val targetLedsBits = targetLeds.toInt(2)
    val buttonLedsBits = buttons.map {
        it.sumOf { pos -> 1 shl (targetLeds.lastIndex - pos) }
    }
}