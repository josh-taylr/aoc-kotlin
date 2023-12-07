fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            line.filter(Char::isDigit).let { "${it.first()}${it.last()}" }.toInt()
        }
    }

    fun part2(input: List<String>): Int {
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

    check(part1(readInput("Day01_test")) == 142)
    check(part2(readInput("Day01_test2")) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
