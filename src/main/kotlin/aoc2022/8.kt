package aoc2022

import readInput
import solveA
import solveB

private fun main() {
    val input = readInput(::main).trim().lines().map { it.toCharArray().map { it.digitToInt() } }
    val forest = Forest(input)
    solveA(::main, forest.getNrVisible())
    solveB(::main, forest.getMaxScenicScore())
}

class Forest(treeHeights: List<List<Int>>) {
    private val trees = treeHeights.mapIndexed { x, row ->
        row.mapIndexed { y, height -> Tree(x, y, height) }
    }

    inner class Tree(val x: Int, val y: Int, val height: Int) {
        fun getTreesNorth() = (x - 1 downTo 0).map { tx -> trees[tx][y] }
        fun getTreesSouth() = (x + 1 until trees.size).map { tx -> trees[tx][y] }
        fun getTreesWest() = (y - 1 downTo 0).map { ty -> trees[x][ty] }
        fun getTreesEast() = (y + 1 until trees.first().size).map { ty -> trees[x][ty] }
        fun getViewingDistance(treeLine: List<Tree>) =
            treeLine.indexOfFirst { it.height >= this.height }.let { if (it == -1) treeLine.size else it + 1 }

        fun isVisibleFromEdge() = getTreesNorth().all { this.height > it.height } ||
                getTreesEast().all { this.height > it.height } ||
                getTreesSouth().all { this.height > it.height } ||
                getTreesWest().all { this.height > it.height }

        fun getScenicScore() = getViewingDistance(getTreesNorth()) *
                getViewingDistance(getTreesEast()) *
                getViewingDistance(getTreesSouth()) *
                getViewingDistance(getTreesWest())
    }

    fun getNrVisible() = trees.flatten().count { it.isVisibleFromEdge() }
    fun getMaxScenicScore() = trees.flatten().maxOf { it.getScenicScore() }
}