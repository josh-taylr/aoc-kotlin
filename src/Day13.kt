typealias Pattern = List<List<Char>>

fun main() {
    fun part1(input: List<String>) = input.toPatterns().map(Pattern::summarise).sum()

    fun part2(input: List<String>) = input.toPatterns().map(Pattern::summariseNewReflections).sum()

    allChecks()
    checkValue(405, part1(readInput("Day13_test")))
    checkValue(400, part2(readInput("Day13_test")))

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}

private fun Pattern.summarise() =
    findMirror() + (transposed().findMirror() * 100)

private fun Pattern.summariseNewReflections() =
    findMirror(diffs = 1) + (transposed().findMirror(diffs = 1) * 100)

private fun Pattern.findMirror(diffs: Int = 0) =
    indices.possibleReflectionRanges()
        .firstOrNull { indices -> diffs == slice(indices).countDiffs() }
        ?.reflectionPoint() ?: 0

private fun Pattern.countDiffs() = sumOf { row -> countDiffsRow(row) }

private fun countDiffsRow(row: List<Char>) =
    (0..<(row.size / 2)).count { i -> row[i] != row[row.lastIndex - i] }

private fun IntRange.reflectionPoint() =
    if (!isEmpty()) (first + (last - first) / 2) + ((last - first) % 2) else 0

private fun IntRange.possibleReflectionRanges(): Set<IntRange> = let { range ->
    buildSet {
        add(range)
        for (i in 1..<range.last) {
            add(0..(range.last - i))
            add(i..range.last)
        }
    }
}

private fun Iterable<String>.toPatterns(): Sequence<Pattern> {
    val iter = iterator()
    return sequence {
        while (iter.hasNext()) {
            val block = buildList {
                while (iter.hasNext()) {
                    val line = iter.next()
                    if (line.isBlank()) break
                    this.add(line.toList())
                }
            }
            yield(block)
        }
    }
}

private fun allChecks() {
    // Iterable<String>.toPatterns()
    checkValue(
        expected = listOf(
            listOf(
                listOf('#', '#'),
                listOf('.', '.'),
                listOf('#', '.'),
            ),
            listOf(
                listOf('.', '#'),
                listOf('#', '#'),
            ),
        ),
        actual = listOf(
            "##",
            "..",
            "#.",
            "",
            ".#",
            "##",
        ).toPatterns().toList()
    )

    // List<String>.transposed()
    checkValue(
        expected = listOf(
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(3, 6, 9),
            ),
        actual = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        ).transposed()
    )


    // IntRange.reflectionPoint()
    checkValue(1, (0..1).reflectionPoint()) // ^##
    checkValue(1, (0..2).reflectionPoint()) // ^#.#
    checkValue(3, (2..3).reflectionPoint()) // ..##$
    checkValue(2, (1..3).reflectionPoint()) // .###$
    checkValue(4, (3..4).reflectionPoint()) // ...##$
    checkValue(3, (2..4).reflectionPoint()) // ..###$

    // List<Char>.countDiffs()
    checkValue(0, countDiffsRow(listOf('#', '#')))
    checkValue(1, countDiffsRow(listOf('#', '.')))
    checkValue(0, countDiffsRow(listOf('.', '.', '.')))
    checkValue(1, countDiffsRow(listOf('#', '.', '.')))
    checkValue(0, countDiffsRow(listOf('.', '#', '#', '.')))
    checkValue(2, countDiffsRow(listOf('.', '#', '.', '#')))

    // IntRange.possibleReflectionRanges()
    checkValue(setOf(0..2, 0..1, 1..2), (0..2).possibleReflectionRanges())
    checkValue(setOf(0..3, 0..2, 1..3, 0..1, 2..3), (0..3).possibleReflectionRanges())
}