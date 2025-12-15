package aoc2021

import readInput

private fun main() {
    edges = readInput(::main).let {
        it.trim().split("\n").map {
            it.trim().split("-").let {
                Edge(it[0], it[1])
            }
        }
    }.toList()
    traverse("start", listOf())
    println(nrPaths)
}

lateinit var edges: List<Edge>
var nrPaths = 0
fun traverse(node: String, path: List<String>) {
    if (node.isSmall() && path.contains(node) && hasDuplicateSmallCave(path)) {
        return
    }
    if (node == "start" && path.contains("start")) {
        return
    }
    if (node == "end") {
        nrPaths++
        return
    }
    node.connectedNodes().forEach {
        traverse(it, path + node)
    }
}

fun hasDuplicateSmallCave(path: List<String>): Boolean {
    return path.filter { it.isSmall() }.let {
        it.size != it.distinct().size
    }
}

data class Edge(val from: String, val to: String) {

}

fun String.isSmall(): Boolean {
    return this.lowercase() == this
}

fun String.connectedNodes(): List<String> {
    return (edges.filter { it.from == this }.map { it.to } + edges.filter { it.to == this }.map { it.from }).distinct()
}