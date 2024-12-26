package aoc2023

import AOCDay
import Vector2D
import aStar
import println

class Day17 : AOCDay(year = "2023", day = "17")  {
    override fun part1(input: List<String>): Int {
        val heatLossMap = HeatLossMap(input)
        val goalBlock = Vector2D(140, 140)
        val results =
            aStar<Crucible>(
                start = Crucible(Vector2D(0, 0), Crucible.RIGHT, blocksTraveled = 0),
                goal = { it.block == goalBlock },
                heuristic = { it.block.distance(goalBlock) },
                neighbors = { it.getNeighbours().filter { it.block in heatLossMap } },
                weight = { _, n -> heatLossMap.getValue(n.block).toDouble() },
            )
        heatLossMap.overlay(results?.drop(1)).println()
        return results?.drop(1)?.sumOf { heatLossMap.getValue(it.block) } ?: 0
    }

    override fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }
}

private class HeatLossMap(input: List<String>) {

    val values =
        List(input.count()) { y -> List(input[y].count()) { x -> input[x][y].digitToInt() } }

    override fun toString() = buildString {
        for (yIdx in values[0].indices) {
            for (xIdx in values.indices) {
                append(values[xIdx][yIdx])
            }
            appendLine()
        }
    }

    fun getValue(block: Vector2D) = block.let { (x, y) -> values[x][y] }

    operator fun contains(block: Vector2D): Boolean {
        return block.inRanges(values.indices, values.first().indices)
    }
}

private data class Crucible(
    val block: Vector2D,
    val direction: Vector2D,
    val blocksTraveled: Int = 1
) {
    val canContinueForward = blocksTraveled < MAX_TRAVELED
    val canContinueUp = direction * -1 != Companion.UP && (direction != Companion.UP || canContinueForward)
    val canContinueRight = direction * -1 != Companion.RIGHT && (direction != Companion.RIGHT || canContinueForward)
    val canContinueDown = direction * -1 != Companion.DOWN && (direction != Companion.DOWN || canContinueForward)
    val canContinueLeft = direction * -1 != Companion.LEFT && (direction != Companion.LEFT || canContinueForward)

    fun getNeighbours() =
        buildSet<Crucible> {
            if (canContinueForward) {
                add(
                    Crucible(
                        block = block + direction,
                        direction = direction,
                        blocksTraveled = blocksTraveled + 1,
                    )
                )
            }
            val toLeft = direction.rotate(-90.0)
            add(
                Crucible(
                    block = block + toLeft,
                    direction = toLeft,
                )
            )
            val toRight = direction.rotate(90.0)
            add(
                Crucible(
                    block = block + toRight,
                    direction = toRight,
                )
            )
        }

    override fun hashCode() =
        listOf(
                block.hashCode(),
                run {
                    listOf(
                            canContinueUp,
                            canContinueRight,
                            canContinueDown,
                            canContinueLeft,
                        )
                        .fold(0) { acc, b -> (acc shl 1) or if (b) 1 else 0 }
                }
            )
            .reduce { acc, hash -> (acc * 31) + hash }

    override fun equals(other: Any?) =
        when (other) {
            is Crucible -> block == other.block &&
                    canContinueUp == other.canContinueUp &&
                    canContinueRight == other.canContinueRight &&
                    canContinueDown == other.canContinueDown &&
                    canContinueLeft == other.canContinueLeft
            else -> false
        }

    companion object {
        const val MAX_TRAVELED = 3
        val UP = Vector2D(0, -1)
        val RIGHT = Vector2D(1, 0)
        val DOWN = Vector2D(0, 1)
        val LEFT = Vector2D(-1, 0)
    }
}

private fun HeatLossMap.overlay(results: List<Crucible>?) = buildString {
    fun Vector2D.toChar() =
        when (this) {
            Crucible.UP -> '^'
            Crucible.RIGHT -> '>'
            Crucible.DOWN -> 'v'
            Crucible.LEFT -> '<'
            else -> error("Encountered unexpected direction: $this")
        }

    for (yIdx in values[0].indices) {
        for (xIdx in values.indices) {
            val crucible = results?.find { it.block == Vector2D(xIdx, yIdx) }
            val value = values[xIdx][yIdx]
            append(crucible?.direction?.toChar() ?: value.digitToChar())
        }
        appendLine()
    }
}