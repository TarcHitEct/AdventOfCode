package aoc2020

import readInput

private fun main() {
    val tiles = readInput(::main).let {
        it.trim().split("Tile").filter { it.isNotBlank() }.map {
            val tile = it.trim().split("\n")
            val id = tile[0].trim().trim(':').toLong()
            Tile(id, (1..10).map { tile[it].trim() })
        }
    }.toList()

    tiles.forEach { tile ->
        val otherEdges = tiles.filter { it != tile }.flatMap { it.edges }
        tile.edges.forEach { edge ->
            otherEdges.singleOrNull { it == edge }?.let { it.nextTile = tile }
        }
    }
    val cornerTiles = tiles.filter { it.edges.count { it.nextTile == null } == 2 }
    require(cornerTiles.size == 4)
    println(cornerTiles[0].id * cornerTiles[1].id * cornerTiles[2].id * cornerTiles[3].id)
}

data class Tile(val id: Long, var pixels: List<String>) {
    init {
        require(pixels.size == 10)
        require(pixels.all { it.length == 10 })
        require(edges.size == 4)
        require(edges.all { it.pixels.length == 10 })
        require(edges.all { it.pixels != it.pixels.reversed() })
    }

    val edges
        get() = getEdges(pixels)
    val topEdge
        get() = edges[0]
    val rightEdge
        get() = edges[3]
    val bottomEdge
        get() = edges[1]
    val leftEdge
        get() = edges[2]
}

private fun getEdges(pixels: List<String>): List<Edge> {
    return listOf(
        pixels.first(), pixels.last(),
        pixels.map { it.first() }.joinToString(""),
        pixels.map { it.last() }.joinToString("")
    ).map { Edge(it) }
}

data class Edge(val pixels: String, var nextTile: Tile? = null) {
    private val canonical: String = listOf(pixels, pixels.reversed()).sorted().joinToString("")

    override fun equals(other: Any?): Boolean {
        return if (other is Edge) {
            this.canonical == other.canonical
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return canonical.hashCode()
    }
}