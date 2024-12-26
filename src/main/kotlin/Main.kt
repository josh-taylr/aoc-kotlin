import aoc2023.*

fun main() {
    val year = System.getenv("AOC_YEAR")?.toIntOrNull() ?: 2023
    val day = System.getenv("AOC_DAY")?.toIntOrNull() ?: 1

    val solution: AOCDay = when (year) {
        2023 -> when (day) {
            1 -> Day01()
            2 -> Day02()
            3 -> Day03()
            4 -> Day04()
            5 -> Day05()
            6 -> Day06()
            7 -> Day07()
            8 -> Day08()
            9 -> Day09()
            10 -> Day10()
            11 -> Day11()
            12 -> Day12()
            13 -> Day13()
            14 -> Day14()
            15 -> Day15()
            16 -> Day16()
            17 -> Day17()
            18 -> Day18()
            else -> error("Day $day not implemented for year $year")
        }
        else -> error("Year $year not implemented")
    }

    val input = readInput("aoc$year/Day${day.toString().padStart(2, '0')}")
    solution.part1(input).println()
    solution.part2(input).println()
}