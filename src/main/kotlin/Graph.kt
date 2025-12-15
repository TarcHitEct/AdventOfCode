interface GraphNode<T : GraphNode<T>> {
    val edges: List<GraphEdge<T>>
    val optimisticCost: Int
        get() = throw NotImplementedError("optimisticCost not implemented")
    val isGoal: Boolean
        get() = throw NotImplementedError("isGoal not implemented")
}

data class GraphEdge<T : GraphNode<T>>(val from: T, val to: T, val weight: Int = 1)

fun <T : GraphNode<T>> T.dijkstra(
    result: DijkstraResult<T> = DijkstraResult(),
    curDistance: Int = 0
): DijkstraResult<T> {
    val minDistance = result.distances.getOrDefault(this, Integer.MAX_VALUE)
    if (curDistance < minDistance) {
        result.distances[this] = curDistance
        edges.forEach { edge ->
            edge.to.dijkstra(result, curDistance + edge.weight)
        }
    }
    return result
}

fun <T : GraphNode<T>> T.reachability(
    result: ReachabilityResult<T> = ReachabilityResult()
): ReachabilityResult<T> {
    var curRoundNodes = setOf(this)
    while (curRoundNodes.isNotEmpty()) {
        result.reachableNodes.addAll(curRoundNodes)
        curRoundNodes = curRoundNodes.flatMap { it.edges.map { it.to } }.filter {
            it !in result.reachableNodes
        }.toSet()
    }
    return result
}

fun <T : GraphNode<T>> T.traverse(
    result: TraverseResult<T> = TraverseResult(),
    path: List<T> = emptyList(),
    onPathFound: (path: List<T>, result: TraverseResult<T>, returning: Boolean) -> Unit
): TraverseResult<T> {
    if (!result.done) {
        val curPath = path + this
        onPathFound(curPath, result, false)
        edges.forEach { edge ->
            edge.to.traverse(result, curPath, onPathFound)
        }
        onPathFound(curPath, result, true)
    }
    return result
}

fun <T : GraphNode<T>> T.aStar(
    result: AStartResult<T> = AStartResult(),
    path: List<GraphEdge<T>> = emptyList(),
): AStartResult<T> {
    if (path.isClosedLoop()) {
        return result
    }
    val minDistance = result.distances.getOrDefault(this.hashCode(), Integer.MAX_VALUE)
    val curDistance = path.cost()
    if (curDistance < minDistance) {
        result.distances[this.hashCode()] = curDistance
    } else {
        //println("dijkstra shortcut")
        return result
    }
    if (isGoal && curDistance < result.shortestDistance) {
        println("Found path with cost $curDistance")
        result.shortestPath = path
        result.shortestDistance = curDistance
        return result
    }
    if (curDistance + optimisticCost >= result.shortestDistance) {
        //println("optimisticCost shortcut")
        return result
    }
    edges.sortedBy { it.to.optimisticCost }.forEach { edge ->
        edge.to.aStar(result, path + edge)
    }
    return result
}

class DijkstraResult<T : GraphNode<T>> {
    val distances = mutableMapOf<T, Int>()
}

class ReachabilityResult<T : GraphNode<T>> {
    val reachableNodes = mutableSetOf<T>()
}

class TraverseResult<T : GraphNode<T>> {
    val relevantPaths = mutableListOf<List<T>>()
    var done = false
}

class AStartResult<T : GraphNode<T>> {
    var shortestPath = listOf<GraphEdge<T>>()
    var shortestDistance = Int.MAX_VALUE
    val distances = mutableMapOf<Int, Int>()
}

fun <T : GraphNode<T>> List<GraphEdge<T>>.cost() = this.sumOf { it.weight }
fun <T : GraphNode<T>> List<GraphEdge<T>>.isClosedLoop() = !isEmpty() && first().from == last().to