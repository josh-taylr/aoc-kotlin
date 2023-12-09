fun main() {
    fun part1(input: List<String>): Int {
        return input.map(String::toIntList).sumOf {
            calcNextNumber(it)
        }
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    checkValue(114, part1(readInput("Day09_test")))

    val input = readInput("Day09")
    part1(input).println()
}

private fun calcNextNumber(numbers: List<Int>): Int {
    val accumulatedDeltas = buildList<List<Int>> {
        add(numbers)
        while (!last().all { it == 0 }) { // while not all 0s
            add(last().deltas())
        }
    }
    return accumulatedDeltas.foldRight(0) { deltas, result ->
        deltas.last() + result
    }
}