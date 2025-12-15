package aoc2021

import readInput

private fun main() {
    val caveTile = readInput(::main).let {
        it.trim().split("\n").map {
            it.toCharArray().map { Spot2(it.toString().toLong(), null) }
        }
    }.toList()

    val cave = (0 until 500).map { x ->
        (0 until 500).map { y ->
            val original = caveTile[x % 100][y % 100]
            val tileX = x / 100
            val tileY = y / 100
            var newRisk = original.riskLevel + tileX + tileY
            if (newRisk > 9) {
                newRisk -= 9
            }
            Spot2(newRisk, null)
        }
    }

    cave[0][0].minScore = 0
    while (propagate(cave)) {
    }
    println(cave.last().last().minScore)
}

fun propagate(cave: List<List<Spot2>>): Boolean {
    var changes = false
    cave.indices.forEach { x ->
        cave.first().indices.forEach { y ->
            val cur = cave[x][y]
            val minNeighbour = listOfNotNull(
                safeGet(cave, x + 1, y),
                safeGet(cave, x, y + 1),
                safeGet(cave, x - 1, y),
                safeGet(cave, x, y - 1)
            ).mapNotNull { it.minScore }
                .filter { it + cur.riskLevel < (cur.minScore ?: Long.MAX_VALUE) }
                .minOrNull()
            if (minNeighbour != null) {
                cur.minScore = minNeighbour + cur.riskLevel
                changes = true
            }
        }
    }
    return changes
}

fun <T> safeGet(list: List<List<T>>, x: Int, y: Int): T? {
    if (x >= 0 && y >= 0 && x < list.size && y < list.first().size) {
        return list[x][y]
    }
    return null
}

data class Spot2(val riskLevel: Long, var minScore: Long?)

