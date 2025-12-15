package aoc2022

import GraphEdge
import GraphNode
import readInput
import solveA
import solveB
import traverse

private fun main() {
    val test = """
        Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
        Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    """.trimIndent()
    val blueprints = readInput(::main).let {
        Regex(
            "Blueprint (\\d+): " +
                    "Each ore robot costs (\\d+) ore. " +
                    "Each clay robot costs (\\d+) ore. " +
                    "Each obsidian robot costs (\\d+) ore and (\\d+) clay. " +
                    "Each geode robot costs (\\d+) ore and (\\d+) obsidian."
        ).findAll(it).map {
            Blueprint(
                it.groupValues[1].toInt(),
                MaterialAmounts(mapOf(Material.ORE to it.groupValues[2].toInt())),
                MaterialAmounts(mapOf(Material.ORE to it.groupValues[3].toInt())),
                MaterialAmounts(
                    mapOf(
                        Material.ORE to it.groupValues[4].toInt(),
                        Material.CLAY to it.groupValues[5].toInt()
                    )
                ),
                MaterialAmounts(
                    mapOf(
                        Material.ORE to it.groupValues[6].toInt(),
                        Material.OBSIDIAN to it.groupValues[7].toInt()
                    )
                )
            )
        }.toList()
    }

    blueprints.sumOf { blueprint ->
        val op = MiningOperation(blueprint, 24)
        val geode = op.traverseResult.relevantPaths.first().last().materials[Material.GEODE]
        println("Blueprint ${blueprint.id} can collect $geode geodes")
        blueprint.id * geode
    }.let {
        solveA(::main, it)
    }

    blueprints.take(3).map { blueprint ->
        val op = MiningOperation(blueprint, 32)
        val geode = op.traverseResult.relevantPaths.first().last().materials[Material.GEODE]
        println("Blueprint ${blueprint.id} can collect $geode geodes")
        geode
    }.reduce { a, b -> a * b }.let {
        solveB(::main, it)
    }
}

class MiningOperation(val blueprint: Blueprint, val timeLimit: Int) {
    val maxCostPerMaterial = blueprint.oreRobotCost
        .mergeMax(blueprint.clayRobotCost)
        .mergeMax(blueprint.obsidianRobotCost)
        .mergeMax(blueprint.geodeRobotCost)
    val startingState = MiningState(MaterialAmounts(mapOf(Material.ORE to 1)), MaterialAmounts(emptyMap()), 1)

    val traverseResult by lazy {
        startingState.traverse { path, result, returning ->
            if (!returning && path.last().minute == timeLimit + 1) {
                val max = result.relevantPaths.firstOrNull()?.last()?.materials?.get(Material.GEODE)
                val current = path.last().materials[Material.GEODE]
                if (max == null || current > max) {
                    result.relevantPaths.clear()
                    result.relevantPaths += path
                }
            }
        }
    }

    fun getMaterialAmountForRobot(robotType: Material) = when (robotType) {
        Material.ORE -> blueprint.oreRobotCost
        Material.CLAY -> blueprint.clayRobotCost
        Material.OBSIDIAN -> blueprint.obsidianRobotCost
        Material.GEODE -> blueprint.geodeRobotCost
    }

    inner class MiningState(val robots: MaterialAmounts, val materials: MaterialAmounts, val minute: Int) :
        GraphNode<MiningState> {
        override val edges: List<GraphEdge<MiningState>>
            get() {
                if (minute == timeLimit + 1) {
                    return emptyList()
                } else if (minute > timeLimit + 1) {
                    throw IllegalStateException("Passed time limit")
                }
                return Material.values().reversed().mapNotNull { robotType ->
                    if (robots[robotType] >= maxCostPerMaterial[robotType] && robotType != Material.GEODE) {
                        return@mapNotNull null
                    }
                    val timeToBuild = timeToBuild(robotType)
                    if (timeToBuild == null || minute + timeToBuild > timeLimit) {
                        return@mapNotNull null
                    }
                    val newMaterials = materials + robots * timeToBuild - getMaterialAmountForRobot(robotType)
                    val newRobots = robots + robotType
                    val newMinigState = MiningState(newRobots, newMaterials, minute + timeToBuild)
                    GraphEdge(this, newMinigState, timeToBuild)
                }.let {
                    if (it.isNotEmpty()) {
                        it
                    } else {
                        val timeLeft = timeLimit - minute + 1
                        val newMaterials = materials + robots * timeLeft
                        val newMinigState = MiningState(robots, newMaterials, minute + timeLeft)
                        listOf(GraphEdge(this, newMinigState, timeLeft))
                    }
                }
            }

        private fun timeToBuild(robotType: Material): Int? {
            val missingAmounts = getMaterialAmountForRobot(robotType) - materials
            return missingAmounts.amounts.maxOf { (material, missingAmount) ->
                if (missingAmount <= 0) {
                    1
                } else if (robots[material] == 0) {
                    return null
                } else {
                    (missingAmount + robots[material] - 1) / robots[material] + 1
                }
            }
        }
    }
}

data class Blueprint(
    val id: Int,
    val oreRobotCost: MaterialAmounts,
    val clayRobotCost: MaterialAmounts,
    val obsidianRobotCost: MaterialAmounts,
    val geodeRobotCost: MaterialAmounts
)

data class MaterialAmounts(val amounts: Map<Material, Int>) {
    fun mergeMax(other: MaterialAmounts): MaterialAmounts {
        val newAmounts = amounts.toMutableMap()
        other.amounts.forEach { (material, amount) ->
            if (newAmounts.getOrDefault(material, 0) < amount) {
                newAmounts[material] = amount
            }
        }
        return MaterialAmounts(newAmounts)
    }

    operator fun get(type: Material) = amounts.getOrDefault(type, 0)
    operator fun plus(type: Material) = MaterialAmounts(amounts.toMutableMap().let {
        val amount = it.getOrDefault(type, 0)
        it[type] = amount + 1
        it
    })

    operator fun plus(other: MaterialAmounts) = MaterialAmounts(
        Material.values().associateWith { type ->
            get(type) + other.get(type)
        }
    )

    operator fun minus(other: MaterialAmounts) = MaterialAmounts(
        Material.values().associateWith { type ->
            get(type) - other.get(type)
        }
    )

    operator fun times(times: Int) = MaterialAmounts(
        amounts.mapValues { it.value * times }
    )
}

enum class Material { ORE, CLAY, OBSIDIAN, GEODE }