package aoc2021

import solveA
import solveB

private fun main() {
    val target = Rectangle(Point(269, -44), Point(292, -68))
    val maxYVel = -target.bottomright.y - 1
    solveA(::main, (maxYVel + 1) * maxYVel / 2)

    val minYVel = target.bottomright.y
    val maxXVel = target.bottomright.x
    val minXVel = 0
    val res = (minXVel..maxXVel).sumOf { xVel ->
        (minYVel..maxYVel).count { yVel ->
            hitsTarget(target, xVel, yVel)
        }
    }
    solveB(::main, res)
}

fun hitsTarget(target: Rectangle, xVelI: Int, yVelI: Int): Boolean {
    var x = 0
    var y = 0
    var xVel = xVelI
    var yVel = yVelI
    while (x <= target.bottomright.x && y >= target.bottomright.y) {
        if (target.contains(x, y)) {
            return true
        }
        x += xVel
        y += yVel
        if (xVel > 0) {
            xVel--
        }
        yVel--
    }
    return false
}

data class Rectangle(val topleft: Point, val bottomright: Point) {
    fun contains(x: Int, y: Int): Boolean {
        return x >= topleft.x && x <= bottomright.x && y >= bottomright.y && y <= topleft.y
    }
}