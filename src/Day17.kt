import Crucible.Companion.DOWN
import Crucible.Companion.LEFT
import Crucible.Companion.RIGHT
import Crucible.Companion.UP

fun main() {
    fun part1(input: List<String>): Int {
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
    val backWards = direction.rotate(180.0)
    val canContinueUp = backWards != UP && (direction != UP || canContinueForward)
    val canContinueRight = backWards != RIGHT && (direction != RIGHT || canContinueForward)
    val canContinueDown = backWards != DOWN && (direction != DOWN || canContinueForward)
    val canContinueLeft = backWards != LEFT && (direction != LEFT || canContinueForward)

    fun getNeighbours() = buildSet<Crucible> {
        if (canContinueForward) {
            add(Crucible(
                    block = block + direction,
                    direction = direction,
                    blocksTraveled = blocksTraveled + 1,
            ))
        }
        val toLeft = direction.rotate(-90.0)
        add(Crucible(
                block = block + toLeft,
                direction = toLeft,
        ))
        val toRight = direction.rotate(90.0)
        add(Crucible(
                block = block + toRight,
                direction = toRight,
        ))
    }

    override fun hashCode() = listOf(
            block.hashCode(),
            direction.hashCode(),
            Integer.valueOf(blocksTraveled).hashCode(),
//            canContinueUp.hashCode(),
//            canContinueRight.hashCode(),
//            canContinueDown.hashCode(),
//            canContinueLeft.hashCode(),
    ).reduce { acc, hash -> (acc * 31) + hash }

    override fun equals(other: Any?) = when (other) {
        is Crucible -> hashCode() == other.hashCode()
//            (this.block == other.block)
//                && (this.direction == other.direction)
//                && (this.canContinueForward == other.canContinueForward)
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

    for (yIdx in values[0].indices) {
        for (xIdx in values.indices) {
            val crucible = results?.find { it.block == Vector2D(xIdx, yIdx) }
            val value = values[xIdx][yIdx]
            append(crucible?.direction?.toChar() ?: value.digitToChar())
        }
        appendLine()
    }
}

private fun allChecks(input: List<String>) {
    val heatLossMap = HeatLossMap(input)
    val goalBlock = Vector2D(12, 12)
    val results = aStar<Crucible>(
            start = Crucible(Vector2D(0, 0), RIGHT),
            goal = { it.block == goalBlock },
            heuristic = { it.block.distance(goalBlock) },
            neighbors = { it.getNeighbours().filter { it.block in heatLossMap } },
            weight = { c, n -> heatLossMap.getValue(n.block).toDouble() },
    )
    heatLossMap.overlay(results?.drop(1)).println()
    checkValue(102, results?.drop(1)?.sumOf { heatLossMap.getValue(it.block) } ?:0 )
}