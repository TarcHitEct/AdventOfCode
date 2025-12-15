package aoc2022

import aoc2021.Point
import readInput
import solveA
import solveB

private fun main() {
    val inputLines = readInput(::main).lines().dropLast(1)
    val pwdBoard = PwdBoard(inputLines.dropLast(2), inputLines.last())
    pwdBoard.tracePath(false)
    solveA(::main, pwdBoard.getPwd())

    val pwdCube = PwdBoard(inputLines.dropLast(2), inputLines.last())
    pwdCube.tracePath(true)
    solveB(::main, pwdCube.getPwd())
}

open class PwdBoard(lines: List<String>, val path: String) {
    val board = mutableMapOf<Point, PwdTile>().also {
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                if (char != ' ') {
                    it[Point(x, y)] = if (char == '#') {
                        PwdTile.WALL
                    } else if (char == '.') {
                        PwdTile.OPEN
                    } else {
                        throw IllegalStateException("Unknown tile $char")
                    }
                }
            }
        }
    }

    val rowIndices = board.keys.groupBy { it.y }.mapValues { (y, points) ->
        val xPoints = points.map { it.x }
        (xPoints.min()..xPoints.max())
    }

    val colIndices = board.keys.groupBy { it.x }.mapValues { (x, points) ->
        val yPoints = points.map { it.y }
        (yPoints.min()..yPoints.max())
    }

    val cubeFaces = listOf(
        Pair(2, 0), Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(0, 2), Pair(0, 3)
    ).mapIndexed { face, (x, y) ->
        PwdCubeFace(face, ((x * 50 until (x + 1) * 50) to (y * 50 until (y + 1) * 50)))
    }

    var curPosition = Point(rowIndices[0]!!.first, 0)
    var curFacing = Facing.RIGHT

    fun tracePath(cube: Boolean) {
        Regex("(\\d+|L|R)").findAll(path).forEach {
            val step = it.groupValues[1]
            if (step.toIntOrNull() != null) {
                repeat(step.toInt()) {
                    if (cube) moveForwardCube() else moveForward()
                }
            } else if (step == "L") {
                curFacing = curFacing.turnLeft()
            } else if (step == "R") {
                curFacing = curFacing.turnRight()
            } else {
                throw IllegalStateException("Unknown step $step")
            }
        }
    }

    private fun moveForward() {
        when (curFacing) {
            Facing.RIGHT -> {
                var newX = curPosition.x + 1
                val indices = rowIndices[curPosition.y]!!
                if (newX > indices.last) {
                    newX = indices.first
                }
                val newPos = Point(newX, curPosition.y)
                if (board[newPos] == PwdTile.OPEN) {
                    curPosition = newPos
                }
            }

            Facing.DOWN -> {
                var newY = curPosition.y + 1
                val indices = colIndices[curPosition.x]!!
                if (newY > indices.last) {
                    newY = indices.first
                }
                val newPos = Point(curPosition.x, newY)
                if (board[newPos] == PwdTile.OPEN) {
                    curPosition = newPos
                }
            }

            Facing.LEFT -> {
                var newX = curPosition.x - 1
                val indices = rowIndices[curPosition.y]!!
                if (newX < indices.first) {
                    newX = indices.last
                }
                val newPos = Point(newX, curPosition.y)
                if (board[newPos] == PwdTile.OPEN) {
                    curPosition = newPos
                }
            }

            Facing.UP -> {
                var newY = curPosition.y - 1
                val indices = colIndices[curPosition.x]!!
                if (newY < indices.first) {
                    newY = indices.last
                }
                val newPos = Point(curPosition.x, newY)
                if (board[newPos] == PwdTile.OPEN) {
                    curPosition = newPos
                }
            }
        }
    }

    private fun moveForwardCube() {
        val curFace = cubeFaces.single { curPosition in it }
        when (curFacing) {
            Facing.RIGHT -> {
                val newPos = curPosition + Point(1, 0)
                val transform = curFace.right
                moveForwardCubeInner(newPos, curFace, transform)
            }

            Facing.DOWN -> {
                val newPos = curPosition + Point(0, 1)
                val transform = curFace.bottom
                moveForwardCubeInner(newPos, curFace, transform)
            }

            Facing.LEFT -> {
                val newPos = curPosition + Point(-1, 0)
                val transform = curFace.left
                moveForwardCubeInner(newPos, curFace, transform)
            }

            Facing.UP -> {
                val newPos = curPosition + Point(0, -1)
                val transform = curFace.top
                moveForwardCubeInner(newPos, curFace, transform)
            }
        }
    }

    private fun moveForwardCubeInner(newPos: Point, curFace: PwdCubeFace, transform: PwdCubeFaceTransform) {
        var newPos1 = newPos
        var newFacing = curFacing
        if (newPos1 !in curFace) {
            val newFace = cubeFaces[transform.newFaceId - 1]
            val offset = curFace.getOffsetOnEdge(curFacing, curPosition).let {
                if (transform.invert) 49 - it else it
            }
            newFacing = transform.newFacing
            newPos1 = newFace.getPositionFromEdge(newFacing, offset)
        }
        if (board[newPos1] == PwdTile.OPEN) {
            curPosition = newPos1
            curFacing = newFacing
        }
    }

    fun getPwd() = (curPosition.y + 1) * 1000 + (curPosition.x + 1) * 4 + curFacing.ordinal
}

data class PwdCubeFace(
    val faceId: Int,
    val indices: Pair<IntRange, IntRange>,
) {
    val top: PwdCubeFaceTransform
    val right: PwdCubeFaceTransform
    val bottom: PwdCubeFaceTransform
    val left: PwdCubeFaceTransform

    init {
        when (faceId + 1) {
            1 -> {
                top = PwdCubeFaceTransform(6, Facing.UP, false)
                right = PwdCubeFaceTransform(4, Facing.LEFT, true)
                bottom = PwdCubeFaceTransform(3, Facing.LEFT, false)
                left = PwdCubeFaceTransform(2, Facing.LEFT, false)
            }

            2 -> {
                top = PwdCubeFaceTransform(6, Facing.RIGHT, false)
                right = PwdCubeFaceTransform(1, Facing.RIGHT, false)
                bottom = PwdCubeFaceTransform(3, Facing.DOWN, false)
                left = PwdCubeFaceTransform(5, Facing.RIGHT, true)
            }

            3 -> {
                top = PwdCubeFaceTransform(2, Facing.UP, false)
                right = PwdCubeFaceTransform(1, Facing.UP, false)
                bottom = PwdCubeFaceTransform(4, Facing.DOWN, false)
                left = PwdCubeFaceTransform(5, Facing.DOWN, false)
            }

            4 -> {
                top = PwdCubeFaceTransform(3, Facing.UP, false)
                right = PwdCubeFaceTransform(1, Facing.LEFT, true)
                bottom = PwdCubeFaceTransform(6, Facing.LEFT, false)
                left = PwdCubeFaceTransform(5, Facing.LEFT, false)
            }

            5 -> {
                top = PwdCubeFaceTransform(3, Facing.RIGHT, false)
                right = PwdCubeFaceTransform(4, Facing.RIGHT, false)
                bottom = PwdCubeFaceTransform(6, Facing.DOWN, false)
                left = PwdCubeFaceTransform(2, Facing.RIGHT, true)
            }

            6 -> {
                top = PwdCubeFaceTransform(5, Facing.UP, false)
                right = PwdCubeFaceTransform(4, Facing.UP, false)
                bottom = PwdCubeFaceTransform(1, Facing.DOWN, false)
                left = PwdCubeFaceTransform(2, Facing.DOWN, false)
            }

            else -> throw IllegalStateException("Invlaid cube face $faceId")
        }
    }

    fun getPositionFromEdge(dir: Facing, offset: Int): Point {
        return when (dir) {
            Facing.RIGHT -> Point(indices.first.first, indices.second.first + offset)
            Facing.DOWN -> Point(indices.first.first + offset, indices.second.first)
            Facing.LEFT -> Point(indices.first.last, indices.second.first + offset)
            Facing.UP -> Point(indices.first.first + offset, indices.second.last)
        }
    }

    fun getOffsetOnEdge(dir: Facing, p: Point): Int {
        return when (dir) {
            Facing.RIGHT, Facing.LEFT -> p.y - indices.second.first
            Facing.DOWN, Facing.UP -> p.x - indices.first.first
        }
    }

    operator fun contains(p: Point) = p.x in indices.first && p.y in indices.second
}

data class PwdCubeFaceTransform(val newFaceId: Int, val newFacing: Facing, val invert: Boolean)

enum class PwdTile { OPEN, WALL }

enum class Facing {
    RIGHT, DOWN, LEFT, UP;

    fun turnRight() = when (this) {
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
        UP -> RIGHT
    }

    fun turnLeft() = when (this) {
        RIGHT -> UP
        DOWN -> RIGHT
        LEFT -> DOWN
        UP -> LEFT
    }
}