import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Long {
        val universe = Universe.parse(input)
        return universe.sumOfShortestPaths()
    }

    fun part2(input: List<String>): Long {
        val universe = Universe.parse(input)
        universe.expansionValue = 1_000_000
        return universe.sumOfShortestPaths()
    }

    checkValue(374, part1(readInput("Day11_test")))

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}

private class Universe(
    private val galaxies: List<Point>,
    private val emptyRows: MutableList<Boolean>,
    private val emptyColumns: MutableList<Boolean>,
    var expansionValue: Int = 2,
) {

    fun sumOfShortestPaths(): Long {
        // sum the shortest paths between every pair of galaxies
        var sum = 0L
        for (i in galaxies.indices) {
            for (j in i + 1 until galaxies.size) {
                val shortest = galaxies[i].manhattanDistance(galaxies[j])
                if (sum <= Long.MAX_VALUE - shortest) {
                    sum += shortest
                } else {
                    throw ArithmeticException("Integer overflow occurred.")
                }
            }
        }
        return sum
    }

    private fun Point.manhattanDistance(other: Point) =
        (emptyColumns.map(::toExpansion).subList(min(x, other.x), max(x, other.x)).sum()) +
            (emptyRows.map(::toExpansion).subList(min(y, other.y), max(y, other.y)).sum())

    private fun toExpansion(isEmpty: Boolean) = if (isEmpty) expansionValue else 1

    companion object {
        fun parse(input: List<String>): Universe {
            val emptyRows = MutableList(input.count()) { true }
            val emptyColumns = MutableList(input.first().count()) { true }
            val galaxies = buildList {
                for (row in input.indices) {
                    for (col in input[row].indices) {
                        when (input[row][col]) {
                            '#' -> {
                                emptyColumns[col] = false
                                emptyRows[row] = false
                                add(Point(col, row))
                            }
                        }
                    }
                }
            }
            return Universe(galaxies, emptyRows, emptyColumns)
        }
    }
}
