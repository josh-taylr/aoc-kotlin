fun main() {
    val year = System.getenv("AOC_YEAR")?.toIntOrNull() ?: 2023
    val day = System.getenv("AOC_DAY")?.toIntOrNull() ?: 1

    val solution: AOCDay = when (year) {
        2023 -> when (day) {
            1 -> aoc2023.Day01()
            2 -> aoc2023.Day02()
            3 -> aoc2023.Day03()
            4 -> aoc2023.Day04()
            5 -> aoc2023.Day05()
            6 -> aoc2023.Day06()
            7 -> aoc2023.Day07()
            8 -> aoc2023.Day08()
            9 -> aoc2023.Day09()
            10 -> aoc2023.Day10()
            11 -> aoc2023.Day11()
            12 -> aoc2023.Day12()
            13 -> aoc2023.Day13()
            14 -> aoc2023.Day14()
            15 -> aoc2023.Day15()
            16 -> aoc2023.Day16()
            17 -> aoc2023.Day17()
            18 -> aoc2023.Day18()
            else -> error("Day $day not implemented for year $year")
        }
        2024 -> when (day) {
            1 -> aoc2024.Day01()
            2 -> aoc2024.Day02()
            else -> error("Day $day not implemented for year $year")
        }
        else -> error("Year $year not implemented")
    }

    val input = readInput("aoc$year/Day${day.toString().padStart(2, '0')}")
    solution.part1(input).println()
    solution.part2(input).println()
}