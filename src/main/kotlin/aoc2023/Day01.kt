package aoc2023

import AOCDay

class Day01 : AOCDay(year = "2023", day = "1") {
    override fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            line.filter(Char::isDigit).let { "${it.first()}${it.last()}" }.toInt()
        }
    }

    override fun part2(input: List<String>): Int {
        val digits =
            mapOf(
                "one" to 1,
                "two" to 2,
                "three" to 3,
                "four" to 4,
                "five" to 5,
                "six" to 6,
                "seven" to 7,
                "eight" to 8,
                "nine" to 9
            )
        fun searchDigit(line: String, findLast: Boolean = false): Int {
            val indices = if (findLast) line.indices.reversed() else line.indices
            for (i in indices) {
                line[i].digitToIntOrNull()?.let { int ->
                    return int
                }
                for (digit in digits.keys) {
                    if (line.regionMatches(i, digit, 0, digit.length)) {
                        return digits[digit]!!
                    }
                }
            }
            return Int.MAX_VALUE
        }
        return input.sumOf { line ->
            val first = searchDigit(line)
            val last = searchDigit(line, findLast = true)
            "$first$last".toInt()
        }
    }
}
