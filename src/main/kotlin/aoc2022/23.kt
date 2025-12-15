package aoc2022

import aoc2021.Point
import readInput
import solveA
import solveB

private fun main() {
    val inputLines = readInput(::main).lines()
    val ed = ElfDiffusion(inputLines)
    repeat(10) { round ->
        ed.move(round)
    }
    solveA(::main, ed.countEmpty())

    val ed2 = ElfDiffusion(inputLines)
    var round = 0
    while (ed2.move(round++)) {
    }
    solveB(::main, round)
}

class ElfDiffusion(inputLines: List<String>) {
    val elves = inputLines.flatMapIndexed { y, line ->
        line.mapIndexed { x, c ->
            Point(x, y) to ElfDiffusionPosition(Point(x, y), c == '#')
        }
    }.toMap().toMutableMap()

    fun move(round: Int): Boolean {
        elves.forEach { it.value.reset() }
        elves.values.filter { it.taken }.forEach { pos ->
            if (pos.getDirections(round).all { it.none { it.taken } }) {
                return@forEach
            }
            for (it in pos.getDirections(round)) {
                if (it.none { it.taken }) {
                    pos.propose(it.first())
                    break;
                }
            }
        }
        var moved = false
        for (it in elves) {
            if (it.value.move()) {
                moved = true
            }
        }
        return moved
    }

    fun countEmpty(): Int {
        val nrElves = elves.count { it.value.taken }
        val minX = elves.values.filter { it.taken }.minOf { it.point.x }
        val maxX = elves.values.filter { it.taken }.maxOf { it.point.x }
        val minY = elves.values.filter { it.taken }.minOf { it.point.y }
        val maxY = elves.values.filter { it.taken }.maxOf { it.point.y }
        return (maxX - minX + 1) * (maxY - minY + 1) - nrElves
    }

    fun print() {
        val minX = elves.values.filter { it.taken }.minOf { it.point.x }
        val maxX = elves.values.filter { it.taken }.maxOf { it.point.x }
        val minY = elves.values.filter { it.taken }.minOf { it.point.y }
        val maxY = elves.values.filter { it.taken }.maxOf { it.point.y }
        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                elves[Point(x, y)].let {
                    if (it?.taken == true) {
                        print("#")
                    } else {
                        print(".")
                    }
                }
            }
            println()
        }
        println()
        println()
    }

    inner class ElfDiffusionPosition(val point: Point, var taken: Boolean = false) {
        var proposed: Int = 0
        var moveTo: ElfDiffusionPosition? = null
        val north by lazy {
            getPositionsByOffset(listOf(Point(0, -1), Point(-1, -1), Point(1, -1)))
        }
        val south by lazy {
            getPositionsByOffset(listOf(Point(0, 1), Point(-1, 1), Point(1, 1)))
        }
        val west by lazy {
            getPositionsByOffset(listOf(Point(-1, 0), Point(-1, -1), Point(-1, 1)))
        }
        val east by lazy {
            getPositionsByOffset(listOf(Point(1, 0), Point(1, -1), Point(1, 1)))
        }

        fun getPositionsByOffset(offsets: List<Point>) = offsets
            .map { point + it }
            .map { elves.computeIfAbsent(it) { key -> ElfDiffusionPosition(Point(key.x, key.y), false) } }

        fun reset() {
            proposed = 0
            moveTo = null
        }

        fun propose(p: ElfDiffusionPosition) {
            moveTo = p
            p.proposed++
        }

        fun move(): Boolean {
            moveTo?.let {
                if (it.proposed == 1) {
                    this.taken = false
                    it.taken = true
                    return true
                }
            }
            return false
        }

        fun getDirections(offset: Int) = listOf(north, south, west, east).let { directions ->
            (offset until offset + 4).map { idx -> directions[idx % 4] }
        }
    }
}
