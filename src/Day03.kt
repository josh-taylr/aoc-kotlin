fun main() {
    fun part1(input: List<String>): Int {
        fun hasNeighbouringSymbol(str: String, range: IntRange): Boolean {
            if (str.isEmpty()) return false
            val safeRange =
                (range.first - 1).coerceIn(str.indices)..(range.last + 1).coerceIn(str.indices)
            return str.substring(safeRange).contains("[^0-9.]".toRegex())
        }

        val regex = "\\d+".toRegex()
        fun findNumbers(str: String): List<Pair<String, IntRange>> =
            regex.findAll(str).map { match -> match.value to match.range }.toList()

        val partNumbers =
            input
                .mapIndexed { i, str ->
                    val above = input.getOrNull(i - 1) ?: ""
                    val below = input.getOrNull(i + 1) ?: ""
                    findNumbers(str)
                        .filter { (_, range) ->
                            hasNeighbouringSymbol(str, range) ||
                                hasNeighbouringSymbol(above, range) ||
                                hasNeighbouringSymbol(below, range)
                        }
                        .map { (number, _) -> number.toInt() }
                }
                .flatten()

        return partNumbers.sum()
    }

    fun part2(input: List<String>): Int {
        val gearRegex = "\\*".toRegex()
        fun findGears(str: String): List<Int> =
            gearRegex.findAll(str).map { match -> match.range.first }.toList()
        fun gearRatio(a: Int, b: Int) = a * b
        fun findNeighbours(
            i: Int,
            input: String,
            above: String? = null,
            below: String? = null
        ): List<Int> {
            if (input.isEmpty()) return emptyList()
            val searchRange = (i - 1).coerceIn(input.indices)..(i + 1).coerceIn(input.indices)
            val regex = "\\d+".toRegex()
            val partNumbers =
                sequenceOf(
                        above?.let { regex.findAll(it) } ?: emptySequence(),
                        regex.findAll(input),
                        below?.let { regex.findAll(it) } ?: emptySequence(),
                    )
                    .flatten()
                    .map { match -> match.value to match.range }
                    .filter { (_, range) -> searchRange.any { i -> i in range } }
                    .map { (number, _) -> number.toInt() }
                    .toList()
                    .takeIf { it.count() == 2 }
                    .orEmpty()
            return partNumbers
        }
        val gears =
            input
                .flatMapIndexed { i, str ->
                    findGears(str).map {
                        findNeighbours(
                            i = it,
                            input = str,
                            above = input.getOrNull(i - 1),
                            below = input.getOrNull(i + 1),
                        )
                    }
                }
                .filter(List<Int>::isNotEmpty)
        val ratios = gears.map { (partA, partB) -> gearRatio(partA, partB) }
        return ratios.sum()
    }

    check(part1(readInput("Day03_test")) == 4361)
    check(part2(readInput("Day03_test2")) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
