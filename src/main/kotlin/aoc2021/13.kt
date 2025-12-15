package aoc2021

import readInput

private fun main() {
    val dots = readInput(::main).let {
        Regex("(\\d*),(\\d*)").findAll(it).map {
            Point(it.groupValues[1].toInt(), it.groupValues[2].toInt())
        }
    }.toList()
    val folds = readInput(::main).let {
        Regex("(.)=(\\d*)").findAll(it).map {
            Fold(it.groupValues[1], it.groupValues[2].toInt())
        }
    }.toList()

    val folded = folds.fold(dots) { acc, fold ->
        fold(fold, acc)
    }
    val w = folded.maxOf { it.x } + 1
    val h = folded.maxOf { it.y } + 1
    repeat(h) { y ->
        repeat(w) { x ->
            val dot = folded.contains(Point(x, y))
            if (dot) print("#") else print(" ")
        }
        println()
    }
}

fun fold(fold: Fold, dots: List<Point>): List<Point> {
    if (fold.axis == "y") {
        val same = dots.filter { it.y < fold.pos }
        val flipped = dots.filter { it.y > fold.pos }.map {
            Point(it.x, fold.pos - (it.y - fold.pos))
        }
        return same + flipped
    } else {
        val same = dots.filter { it.x < fold.pos }
        val flipped = dots.filter { it.x > fold.pos }.map {
            Point(fold.pos - (it.x - fold.pos), it.y)
        }
        return same + flipped
    }
}

data class Fold(val axis: String, val pos: Int)