package aoc2022

import readInput
import solveA
import solveB

private fun main() {
    val test = """
        1
        2
        -3
        3
        -2
        0
        4
    """.trimIndent()
    val numbers = readInput(::main).trim().lines().map { it.toLong() }
    val file = EncryptedFile(numbers, 1)
    file.mix()
    val zeroIdx = file.positions.indexOfFirst { it.n == 0L }
    (1000L..3000L step 1000L).sumOf {
        file.positions[wrapIndex(zeroIdx + it, file.positions.size)].n
    }.let {
        solveA(::main, it)
    }

    val file2 = EncryptedFile(numbers, 811589153)
    repeat(10) { file2.mix() }
    val zeroIdx2 = file2.positions.indexOfFirst { it.n == 0L }
    (1000L..3000L step 1000L).sumOf {
        file2.positions[wrapIndex(zeroIdx2 + it, file2.positions.size)].n
    }.let {
        solveB(::main, it)
    }
}

class EncryptedFile(numbers: List<Long>, key: Long) {
    val positions = numbers.mapIndexed { idx, n -> EncryptedFilePosition(idx, n * key) }.toMutableList()

    fun mix() {
        positions.indices.forEach { idx ->
            val curPosition = positions.single { it.originalIndex == idx }
            val moveBy = curPosition.n
            val newIdx = wrapIndex(positions.indexOf(curPosition) + moveBy, positions.size - 1)
            positions.remove(curPosition)
            positions.add(newIdx, curPosition.copy(timesMixed = curPosition.timesMixed + 1))
        }
    }
}

data class EncryptedFilePosition(val originalIndex: Int, val n: Long, val timesMixed: Int = 0)

fun wrapIndex(i: Long, iMax: Int): Int {
    return (((i % iMax) + iMax) % iMax).toInt()
}