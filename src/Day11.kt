import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val rowMultiplier = MutableList(input.count()) { 2 }
        val colMultiplier = MutableList(input.first().count()) { 2 }
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

        // sum the shortest paths between every pair of galaxies
        var sum = 0
        for (i in galaxies.indices) {
            for (j in i + 1 until galaxies.size) {
                // the shortest path between this pair of galaxies
                sum += galaxies[i].manhattanDistance(galaxies[j], rowMultiplier, colMultiplier)
            }
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    checkValue(374, part1(readInput("Day11_test")))

    val input = readInput("Day11")
    part1(input).println()
}

private fun Point.manhattanDistance(
    other: Point,
    rowMultiplier: List<Int>,
    colMultiplier: List<Int>
) =
    (colMultiplier.subList(min(x, other.x), max(x, other.x)).sum()) +
        (rowMultiplier.subList(min(y, other.y), max(y, other.y)).sum())
