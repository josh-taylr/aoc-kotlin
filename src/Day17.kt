import Crucible.Companion.DOWN
import Crucible.Companion.LEFT
import Crucible.Companion.RIGHT
import Crucible.Companion.UP

fun main() {
    fun part1(input: List<String>): Int {
        val heatLossMap = HeatLossMap(input)
        val goalBlock = Vector2D(140, 140)
        val results = aStar<Crucible>(
                start = Crucible(Vector2D(0, 0), RIGHT, blocksTraveled = 0),
                goal = { it.block == goalBlock },
                heuristic = { it.block.distance(goalBlock) },
                neighbors = { it.getNeighbours().filter { it.block in heatLossMap } },
                weight = { _, n -> heatLossMap.getValue(n.block).toDouble() },
        )
//        heatLossMap.overlay(results?.drop(1)).println()
//        return results?.drop(1)?.sumOf { heatLossMap.getValue(it.block) } ?:0
        return Int.MAX_VALUE
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    allChecks(readInput("Day17_test"))

    val input = readInput("Day17")
    part1(input).println()
}

private class HeatLossMap(input: List<String>) {

    val values = List(input.count()) { y ->
        List(input[y].count()) { x -> input[x][y].digitToInt() }
    }

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
    val canContinueUp = direction * -1 != UP && (direction != UP || canContinueForward)
    val canContinueRight = direction * -1 != RIGHT && (direction != RIGHT || canContinueForward)
    val canContinueDown = direction * -1 != DOWN && (direction != DOWN || canContinueForward)
    val canContinueLeft = direction * -1 != LEFT && (direction != LEFT || canContinueForward)

    fun getNeighbours() = buildSet<Crucible> {
        if (blocksTraveled < MAX_TRAVELED) for (n in blocksTraveled..<MAX_TRAVELED) {
            add(Crucible(
                    block = block + (direction * n),
                    direction = direction,
                    blocksTraveled = blocksTraveled + n,
            ))
        }
        val toLeft = direction.rotate(-90.0)
        for (n in 1..<MAX_TRAVELED) {
            add(Crucible(
                    block = block + (toLeft * n),
                    direction = toLeft,
                    blocksTraveled = n,
            ))
        }
        val toRight = direction.rotate(90.0)
        for (n in 1..<MAX_TRAVELED) {
            add(Crucible(
                    block = block + toRight,
                    direction = toRight,
                    blocksTraveled = n,
            ))
        }
    }

    override fun hashCode() = listOf(
            block.hashCode(),
            direction.hashCode(),
            blocksTraveled.hashCode(),
//            run {
//                listOf(
//                        canContinueUp,
//                        canContinueRight,
//                        canContinueDown,
//                        canContinueLeft,
//                ).fold(0) { acc, b -> (acc shl 1) or if (b) 1 else 0 }
//            }
    ).reduce { acc, hash -> (acc * 31) + hash }

    override fun equals(other: Any?) = when (other) {
        is Crucible ->
            block == other.block
            && direction == other.direction
            && blocksTraveled == other.blocksTraveled
//            && canContinueUp == other.canContinueUp
//            && canContinueRight == other.canContinueRight
//            && canContinueDown == other.canContinueDown
//            && canContinueLeft == other.canContinueLeft
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
    fun Vector2D.toChar() = when(this) {
        UP -> '^'
        RIGHT -> '>'
        DOWN -> 'v'
        LEFT -> '<'
        else -> error("Encountered unexpected direction: $this")
    }
    val blocks = results?.windowed(2, 1)?.map { (a, b) -> a.block.pointBetween(b.block).map { it to b.direction } }?.flatten()
    for (yIdx in values[0].indices) {
        for (xIdx in values.indices) {
            val pair = blocks?.find { it.first == Vector2D(xIdx, yIdx) }
            val value = values[xIdx][yIdx]
            append(pair?.second?.toChar() ?: value.digitToChar())
        }
        appendLine()
    }
}

private fun allChecks(input: List<String>) {
    val heatLossMap = HeatLossMap(input)
    val goalBlock = Vector2D(12, 12)
    val results = aStar<Crucible>(
            start = Crucible(Vector2D(0, 0), RIGHT, blocksTraveled = 0),
            goal = { it.block == goalBlock },
            heuristic = { it.block.distance(goalBlock) },
            neighbors = { it.getNeighbours().filter { it.block in heatLossMap } },
            weight = { c, n ->
                c.block.pointBetween(n.block).sumOf { (x, y) ->
                    heatLossMap.getValue(Point(x, y)).toDouble()
                } },
    )
    heatLossMap.overlay(results?.drop(1)).println()
    val r = results?.windowed(2, 1)?.map { (a, b) -> a.block.pointBetween(b.block).map { heatLossMap.getValue(it) } }?.flatten()
    checkValue(102, r?.sum() ?:0)
}