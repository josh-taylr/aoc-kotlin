typealias Pattern = List<List<Char>>

fun main() {
    fun part1(input: List<String>) = input.toPatterns().map(Pattern::summarise).sum()

    fun part2(input: List<String>) = input.toPatterns().map(Pattern::summariseNewReflections).sum()

    allChecks()
    checkValue(709, part1(readInput("Day13_test")))
//    checkValue(1400, part2(readInput("Day13_test")))

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}

private fun Pattern.summarise(): Int {
    val rowValue = findMirror() * 100
    val columnValue = transposed().findMirror()
    if (rowValue != 0 && columnValue != 0) error("No reflection point.")
    return rowValue + columnValue
}

private fun Pattern.summariseNewReflections(): Int {
    val height = this.size
    val width = this.first().size
    if (this.any { it.size != width })
        throw IllegalArgumentException("All nested lists must have the same size")

    fun Char.swap() = if(this == '#') '.' else '#'

    for (row in 0..<height) {
        for (col in 0..<width) {
            val newPattern = this.toMutablePattern()
            newPattern[row][col] = newPattern[row][col].swap()
            newPattern.joinToString(
                separator = System.lineSeparator(),
                postfix = System.lineSeparator(),
            ) { line ->
                line.joinToString("")
            }.println()
            val rowValue = newPattern.findMirror() * 100
            val columnValue = newPattern.transposed().findMirror()

            if (rowValue != 0 && columnValue != 0) return rowValue + columnValue
        }
    }
    error("Not implemented")
}

private fun Pattern.findMirror(diffs: Int = 0) =
        indices.possibleReflectionRanges()
            .firstOrNull { rowIdxs -> diffs == slice(rowIdxs).countDiffs() }
            ?.reflectionPoint() ?: 0

private fun Pattern.countDiffs(): Int {
    if (isEmpty()) return 0

    val height = this.size
    val width = this.first().size
    if (this.any { it.size != width })
        throw IllegalArgumentException("All nested lists must have the same size")

    var count = 0
    for (col in 0..<width) {
        for (i in 0..<(height / 2)) {
            if (this[i][col] != this[height - 1 - i][col]) count += 1
        }
    }
    return count
}

private fun IntRange.reflectionPoint() =
    if (!isEmpty()) (count() / 2) + first() else 0

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


fun Pattern.toMutablePattern(): List<MutableList<Char>> = map { it.toMutableList() }

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
    checkValue(2 , (1..3).reflectionPoint()) // .###$
    checkValue(4, (3..4).reflectionPoint()) // ...##$
    checkValue(3, (2..4).reflectionPoint()) // ..###$

    // IntRange.possibleReflectionRanges()
    checkValue(setOf(0..2, 0..1, 1..2), (0..2).possibleReflectionRanges())
    checkValue(setOf(0..3, 0..2, 1..3, 0..1, 2..3), (0..3).possibleReflectionRanges())

    val evenSymmetric = """
        #..
        ##.
        ##.
        #..
    """.trimIndent()
        .lines()
        .toPatterns()
        .single()

    val evenAsymmetric = """
        #.#
        ##.
        ##.
        #..
    """.trimIndent()
            .lines()
            .toPatterns()
            .single()

    val oddSymmetric = """
        #..
        ##.
        #..
    """.trimIndent()
        .lines()
        .toPatterns()
        .single()

    val oddAsymmetric = """
        #.#
        ##.
        #..
    """.trimIndent()
            .lines()
            .toPatterns()
            .single()

    val last = """
        ..#
        #.#
        #.#
    """.trimIndent()
        .lines()
        .toPatterns()
        .single()

    // Pattern.countDiffs()
    checkValue(0, evenSymmetric.countDiffs())
    checkValue(1, evenAsymmetric.countDiffs())
    checkValue(0, oddSymmetric.countDiffs())
    checkValue(1, oddAsymmetric.countDiffs())
    checkValue(2, last.countDiffs())

    // Pattern.findMirror
    checkValue(2, evenSymmetric.findMirror())
    checkValue(0, evenAsymmetric.findMirror())
    checkValue(1, oddSymmetric.findMirror())
    checkValue(0, oddAsymmetric.findMirror())
//    checkValue(2, evenAsymmetric.findMirror(diffs = 1))
//    checkValue(1, oddAsymmetric.findMirror(diffs = 1))
    checkValue(2, last.findMirror())

    val test1 = """
        #....#..#
        #....#..#
        ..##..###
        #####.##.
        #####.##.
        ..##..###
        #....#..#
    """.trimIndent()
            .lines()
            .toPatterns()
            .single()

    checkValue(4, test1.findMirror())
//    checkValue(3, test1.transposed().findMirror())
}