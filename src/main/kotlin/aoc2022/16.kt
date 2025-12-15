package aoc2022

import GraphEdge
import GraphNode
import dijkstra
import readInput
import solveA
import solveB
import kotlin.math.max

private fun main() {
    val tunnelSystem = TunnelSystem()
    readInput(::main).let {
        Regex("Valve (.+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)").findAll(it).forEach {
            tunnelSystem.addValve(
                it.groupValues[1], it.groupValues[2].toInt(), it.groupValues[3].split(",").map { it.trim() }.toSet()
            )
        }
    }
    tunnelSystem.traverse()
    solveA(::main, tunnelSystem.bestScoreA)
    val time1 = System.currentTimeMillis()
    tunnelSystem.traverse2()
    val time2 = System.currentTimeMillis()
    println("Time: ${(time2 - time1) / 1000}s")
    solveB(::main, tunnelSystem.bestScoreB)
}

class TunnelSystem {
    val valves = mutableListOf<Valve>()
    val start by lazy { valves.single { it.name == "AA" }.relvantValve }

    fun addValve(name: String, flowRate: Int, connectedTo: Set<String>) {
        valves.add(Valve(name, flowRate, connectedTo))
    }

    inner class Valve(val name: String, val flowRate: Int, val connectedTo: Set<String>) : GraphNode<Valve> {
        val connectedValves by lazy { valves.filter { it.name in connectedTo } }
        val distanceMap by lazy { this.dijkstra().distances }
        val relvantValve by lazy {
            if (flowRate <= 0 && name != "AA") {
                throw IllegalStateException("Valve $name is not relevant")
            }
            RelvantValve(this)
        }

        override val edges: List<GraphEdge<Valve>>
            get() = connectedValves.map {
                GraphEdge(this, it)
            }
    }

    inner class RelvantValve(val valve: Valve) : GraphNode<RelvantValve> {
        override val edges by lazy {
            valve.distanceMap.filterKeys { it.flowRate > 0 }.map {
                GraphEdge(this, it.key.relvantValve, it.value + 1)
            }
        }
    }

    var bestScoreA: Int = 0
    var bestScoreB: Int = 0

    fun traverse() {
        start.traverse()
    }

    fun TunnelSystem.RelvantValve.traverse(
        path: List<GraphEdge<TunnelSystem.RelvantValve>> = emptyList(),
        thisEdge: GraphEdge<TunnelSystem.RelvantValve>? = null
    ) {
        if (path.any { it.from == this || it.to == this }) {
            return
        }
        val curPath = if (thisEdge != null) path + thisEdge else path
        if (curPath.sumOf { it.weight } >= 30) {
            return
        }
        val score = pathScore(curPath, 30)
        if (score > bestScoreA) {
            bestScoreA = score
            println("best score: $score, depth: ${curPath.size}")
        }
        edges.forEach { edge ->
            edge.to.traverse(curPath, edge)
        }
    }

    fun traverse2(
        node: TunnelSystem.RelvantValve = start,
        node2: TunnelSystem.RelvantValve = start,
        path: List<GraphEdge<TunnelSystem.RelvantValve>> = emptyList(),
        path2: List<GraphEdge<TunnelSystem.RelvantValve>> = emptyList(),
        thisEdge: GraphEdge<TunnelSystem.RelvantValve>? = null,
        thisEdge2: GraphEdge<TunnelSystem.RelvantValve>? = null
    ) {
        if (path.any { it.to == thisEdge?.to || it.to == thisEdge2?.to }) {
            return
        }
        if (path2.any { it.to == thisEdge?.to || it.to == thisEdge2?.to }) {
            return
        }
        val curPath = if (thisEdge != null) path + thisEdge else path
        val curPath2 = if (thisEdge2 != null) path2 + thisEdge2 else path2
        val minutesPassed = curPath.sumOf { it.weight }
        val minutesPassed2 = curPath2.sumOf { it.weight }
        if (minutesPassed >= 26) {
            return
        }
        if (minutesPassed2 >= 26) {
            return
        }
        val score = pathScore(curPath, 26) + pathScore(curPath2, 26)
        if (score > bestScoreB) {
            bestScoreB = score
            println("best score: $score, minutes: ${minutesPassed}, ${minutesPassed2}")
        }
        if (curPath.size > 0 && curPath2.size > 0) {
            val hScore = heuristicScore(curPath, curPath2, 26, valves)
            if (hScore + score < bestScoreB) {
                return
            }
        }
        if (minutesPassed < minutesPassed2) {
            node.edges.forEach { newEdge ->
                traverse2(newEdge.to, node2, curPath, curPath2, newEdge, null)
            }
            node2.edges.forEach { newEdge2 ->
                traverse2(node, newEdge2.to, curPath, curPath2, null, newEdge2)
            }
        } else {
            node2.edges.forEach { newEdge2 ->
                traverse2(node, newEdge2.to, curPath, curPath2, null, newEdge2)
            }
            node.edges.forEach { newEdge ->
                traverse2(newEdge.to, node2, curPath, curPath2, newEdge, null)
            }
        }
    }

    fun pathScore(path: List<GraphEdge<TunnelSystem.RelvantValve>>, deadline: Int): Int {
        var score = 0;
        var minute = 0;
        path.forEach {
            minute += it.weight
            score += it.to.valve.flowRate * (deadline - minute)
        }
        if (minute > deadline) {
            throw IllegalStateException("Path too long")
        }
        return score
    }

    fun heuristicScore(
        path: List<GraphEdge<RelvantValve>>,
        path2: List<GraphEdge<RelvantValve>>,
        deadline: Int,
        valves: MutableList<Valve>
    ): Int {
        val leftToOpen = valves.filter { valve ->
            valve.flowRate > 0 &&
                    path.none { it.to == valve.relvantValve } &&
                    path2.none { it.to == valve.relvantValve }
        }
        return leftToOpen.sumOf {
            val dist1 = it.distanceMap[path.last().to.valve]!!
            val score1 = (deadline - path.sumOf { it.weight } - dist1) * it.flowRate
            val dist2 = it.distanceMap[path2.last().to.valve]!!
            val score2 = (deadline - path2.sumOf { it.weight } - dist2) * it.flowRate
            max(max(score1, score2), 0)
        }
    }
}