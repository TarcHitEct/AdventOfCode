package aoc2025

import GraphEdge
import GraphNode
import feedForward
import reachability
import readInput
import solveA

private fun main() {
    val connections = readInput(::main).trim().split("\n").map { line ->
        val devices = Regex("\\w+").findAll(line).map { it.value }.toList()
        devices.first() to devices.drop(1)
    }.toMap()

    val graph = Graph(connections)
    val start = graph.nodes.single { it.name == "you" }
    val end = graph.nodes.single { it.name == "out" }
    val paths = start.feedForward(start.reachability().reachableNodes, 1) { inputs ->
        inputs.sum()
    }.getValue(end)

    solveA(::main, paths)
    //solveB(::main, resultB)
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