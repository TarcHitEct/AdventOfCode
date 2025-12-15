package aoc2025

import GraphEdge
import GraphNode
import feedForward
import reachability
import readInput
import solveA
import solveB

private fun main() {
    val connections = readInput(::main).trim().split("\n").map { line ->
        val devices = Regex("\\w+").findAll(line).map { it.value }.toList()
        devices.first() to devices.drop(1)
    }.toMap()

    val graph = Graph(connections)
    val start = graph.nodes.single { it.name == "you" }
    val out = graph.nodes.single { it.name == "out" }
    val paths = findPaths(start, out, 1)
    solveA(::main, paths)

    val svr = graph.nodes.single { it.name == "svr" }
    val dac = graph.nodes.single { it.name == "dac" }
    val fft = graph.nodes.single { it.name == "fft" }
    val p1 = findPaths(svr, fft, 1)
    val p2 = findPaths(fft, dac, p1)
    val p3 = findPaths(dac, out, p2)
    solveB(::main, p3)
}

private fun findPaths(start: Graph.Node, end: Graph.Node, input: Long): Long {
    return start.feedForward(start.reachability().reachableNodes, input) { inputs ->
        inputs.sum()
    }.getValue(end)
}

class Graph(val connections: Map<String, List<String>>) {
    val nodes = (connections.keys + connections.values.flatten())
        .toSet().map { Node(it) }

    inner class Node(val name: String) : GraphNode<Node> {
        override val edges by lazy {
            connections[name]?.map { con -> GraphEdge(this, nodes.single { it.name == con }) } ?: listOf()
        }
    }
}