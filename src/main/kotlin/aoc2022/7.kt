package aoc2022

import readInput
import solveA
import solveB

private val root = Directory(null, "/")
private var curDir = root
private fun main() {
    val input = readInput(::main).trim().lines().toMutableList()
    while (input.isNotEmpty()) {
        readCommand(input)
    }

    root.getAllDirectories().filter { it.getSize() <= 100000 }.sumOf { it.getSize() }.let {
        solveA(::main, it)
    }

    val diskSpace = 70000000
    val neededSpace = 30000000
    val freeSpace = diskSpace - root.getSize()
    val spaceToFree = neededSpace - freeSpace
    root.getAllDirectories().filter { it.getSize() >= spaceToFree }.minOf { it.getSize() }.let {
        solveB(::main, it)
    }
}

private fun readCommand(input: MutableList<String>) {
    val cmd = input.removeFirst()
    if (cmd.startsWith("$ cd")) {
        val goTo = cmd.substring(5)
        curDir = when (goTo) {
            "/" -> root
            ".." -> curDir.parent!!
            else -> curDir.dirs.find { it.name == goTo }!!
        }
    } else if (cmd.startsWith("$ ls")) {
        readDirectory(input)
    } else {
        throw IllegalArgumentException("Unknown cmd $cmd")
    }
}

fun readDirectory(input: MutableList<String>) {
    while (input.isNotEmpty() && !input.first().startsWith("$")) {
        val (dirOrSize, name) = input.removeFirst().split(" ")
        if (dirOrSize == "dir") {
            curDir.dirs.add(Directory(curDir, name))
        } else {
            curDir.files.add(File(curDir, name, dirOrSize.toInt()))
        }
    }
}

data class Directory(val parent: Directory?, val name: String) {
    val dirs = mutableSetOf<Directory>()
    val files = mutableSetOf<File>()

    fun getAllDirectories(): List<Directory> {
        return dirs.flatMap { it.getAllDirectories() } + this
    }

    fun getSize(): Int {
        return dirs.sumOf { it.getSize() } + files.sumOf { it.size }
    }
}

data class File(val parent: Directory, val name: String, val size: Int)