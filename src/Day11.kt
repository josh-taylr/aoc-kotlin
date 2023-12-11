import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Long {
        val universe = Universe.parse(2, input)
        return universe.sumOfShortestPaths()
    }

    fun part2(input: List<String>): Long {
        val universe = Universe.parse(1_000_000, input)
        return universe.sumOfShortestPaths()
    }

    checkValue(374, part1(readInput("Day11_test")))

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}

private class Universe(
        val galaxies: List<Point>,
        val rowMultiplier: List<Int>,
        val colMultiplier: List<Int>,
) {

    fun sumOfShortestPaths(): Long {
        // sum the shortest paths between every pair of galaxies
        var sum = 0L
        for (i in galaxies.indices) {
            for (j in i + 1 until galaxies.size) {
                val shortest = galaxies[i].manhattanDistance(galaxies[j], rowMultiplier, colMultiplier)
                if (sum <= Long.MAX_VALUE - shortest) {
                    sum += shortest
                } else {
                    throw ArithmeticException("Integer overflow occurred.")
                }
            }
        }
        return sum
    }

    companion object {
        fun parse(expansionValue: Int, input: List<String>): Universe {
            val rowMultiplier = MutableList(input.count()) { expansionValue }
            val colMultiplier = MutableList(input.first().count()) { expansionValue }
            val galaxies = buildList {
                for (row in input.indices) {
                    for (col in input[row].indices) {
                        when (input[row][col]) {
                            '#' -> {
                                colMultiplier[col] = 1
                                rowMultiplier[row] = 1
                                add(Point(col, row))
                            }
                        }
                    }
                }
            }
            return Universe(galaxies, rowMultiplier, colMultiplier)
        }
    }
}

private fun Point.manhattanDistance(
    other: Point,
    rowMultiplier: List<Int>,
    colMultiplier: List<Int>
) =
    (colMultiplier.subList(min(x, other.x), max(x, other.x)).sum()) +
        (rowMultiplier.subList(min(y, other.y), max(y, other.y)).sum())
