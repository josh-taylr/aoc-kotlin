fun main() {
    fun part1(input: List<String>) = input.map(String::toIntList).sumOf { it.extrapolate().last() }

    fun part2(input: List<String>) = input.map(String::toIntList).sumOf { it.extrapolate().first() }

    checkValue(114, part1(readInput("Day09_test")))

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

private fun List<Int>.extrapolate(): List<Int> =
    accumulateDeltas().reduceRight { numbers, deltas ->
        buildList {
            add(numbers.first() - deltas.first())
            addAll(numbers)
            add(numbers.last() + deltas.last())
        }
    }

private fun List<Int>.accumulateDeltas() =
    generateSequence(this) { it.deltas().takeUnless { deltas -> deltas.all { n -> n == 0 } } }
        .toList()
