package aoc2023

import AOCDay
import transposed
import println

typealias Pattern = List<List<Char>>

class Day13 : AOCDay(year = "2023", day = "13")  {
    override fun part1(input: List<String>) = input.toPatterns().map(Pattern::summarise).sum()

    override fun part2(input: List<String>) = input.toPatterns().map(Pattern::summariseNewReflections).sum()
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