package aoc2022

import aoc2021.Point
import readInput
import solveA
import solveB

val shapes = """
    ####

    .#.
    ###
    .#.

    ..#
    ..#
    ###

    #
    #
    #
    #

    ##
    ##
""".trimIndent()

private fun main() {
    val jets = readInput(::main).trim()
    //val jets = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"
    val rocks = shapes.split(Regex("\n\n")).map {
        it.trim().lines().flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c == '#') Point(x, it.trim().lines().size - y - 1) else null
            }
        }
    }

    val chamber = Chamber(7, 2, 3, rocks, jets)
    chamber.dropRocks(2022)
    solveA(::main, chamber.getTowerHeight())

    val n = 1000000000000
    val repeatStart = chamber.foundRepetition!!.first.nthRock
    val repeatEnd = chamber.foundRepetition!!.second.nthRock
    val repeatSize = repeatEnd - repeatStart
    val repeatHeight = chamber.foundRepetition!!.second.height - chamber.foundRepetition!!.first.height
    val nrRepeats = (n - repeatStart) / repeatSize
    val leftAfterRepeats = n - (repeatStart + nrRepeats * repeatSize)
    val chamber2 = Chamber(7, 2, 3, rocks, jets)
    chamber2.dropRocks((repeatStart + leftAfterRepeats).toInt())
    val height = chamber2.getTowerHeight() + nrRepeats * repeatHeight
    solveB(::main, height)
}

class Chamber(
    val width: Int,
    val spawnDistanceX: Int,
    val spawnDistanceY: Int,
    val rockTypes: List<List<Point>>,
    val jets: String
) {
    val restingRocks = mutableListOf<Point>()
    val repetitions = mutableMapOf<RepetitionPoint, RepetitionInfo>()
    var foundRepetition: Pair<RepetitionInfo, RepetitionInfo>? = null
    fun getSpawnPoint() = Point(spawnDistanceX, getTowerHeight() + spawnDistanceY)
    fun getTowerHeight() = (restingRocks.maxOfOrNull { it.y }?.let { it + 1 } ?: 0)

    fun dropRocks(n: Int) {
        var nthJet = 0
        repeat(n) { nthRock ->
            val rock = spawnRock(nthRock)
            while (!rock.isResting) {
                rock.applyJetPush(nthJet++)
                rock.fall()
            }
            restingRocks += rock.getRockPositions()
            checkRepetitionAndCleanup(nthRock + 1, nthJet)
        }
    }

    private fun checkRepetitionAndCleanup(nthRock: Int, nthJet: Int) {
        val highestRock = getTowerHeight() - 1
        val newFloorOffsets = (0 until width).map { posX ->
            restingRocks.findLast { it.x == posX }?.y ?: -1
        }
        if (foundRepetition == null) {
            val rPoint = RepetitionPoint(nthRock % 5, nthJet % jets.length, newFloorOffsets.map { it - highestRock })
            val rInfo = RepetitionInfo(nthRock, getTowerHeight())
            repetitions[rPoint]?.let {
                println("Repetition between $it and $rInfo")
                foundRepetition = it to rInfo
            }
            repetitions[rPoint] = rInfo
        }
        //cleanup
        if (newFloorOffsets.all { it <= highestRock && it >= highestRock - 3 }) {
            restingRocks.removeAll {
                it.y < highestRock - 3
            }
        }
    }

    fun spawnRock(nthRock: Int) = FallingRock(getSpawnPoint(), rockTypes[nthRock % 5])

    inner class FallingRock(var position: Point, val shape: List<Point>) {
        var isResting = false

        fun fall() {
            val newPosition = position + Point(0, -1)
            if (isValidPosition(newPosition)) {
                position = newPosition;
            } else {
                isResting = true
            }
        }

        fun applyJetPush(nthPush: Int) {
            if (jets[nthPush % jets.length] == '<') {
                val newPosition = position + Point(-1, 0)
                if (isValidPosition(newPosition)) {
                    position = newPosition;
                }
            } else if (jets[nthPush % jets.length] == '>') {
                val newPosition = position + Point(1, 0)
                if (isValidPosition(newPosition)) {
                    position = newPosition;
                }
            } else {
                throw IllegalStateException("Unknown jet direction")
            }
        }

        fun isValidPosition(otherPosition: Point): Boolean {
            return getRockPositions(otherPosition).none { rockPosition ->
                rockPosition.x < 0 || rockPosition.y < 0 || rockPosition.x >= width ||
                        restingRocks.any { restingRock ->
                            restingRock == rockPosition
                        }
            }
        }

        fun getRockPositions(atPostion: Point = position) = shape.map { it + atPostion }
    }
}

data class RepetitionPoint(val nthRock: Int, val nthJet: Int, val floorOffsets: List<Int>)
data class RepetitionInfo(val nthRock: Int, val height: Int)