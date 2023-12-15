fun main() {
    fun part1(input: List<String>): Int {
        return input.toPatterns()
                .map(List<String>::summarise)
                .sum()
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    allChecks()
    checkValue(405, part1(readInput("Day13_test")))

    val input = readInput("Day13")
    part1(input).println()
}

private fun Iterable<String>.toPatterns(): List<List<String>> = buildList {
    val iterator = this@toPatterns.iterator()
    while (iterator.hasNext()) {
        val block = buildList {
            while (iterator.hasNext()) {
                val line = iterator.next()
                if (line.isBlank()) break
                this.add(line)
            }
        }
        this.add(block)
    }
}

private fun List<String>.summarise() = findMirrorColumn() + (findMirrorRow() * 100)

private fun List<String>.findMirrorRow(): Int {
    val acceptableIndices = indices.allRanges()
    return acceptableIndices.firstOrNull { indices ->
        slice(indices).isPalindrome()
    }?.reflectionPoint() ?: 0
}

private fun List<String>.findMirrorColumn(): Int {
    val acceptableIndices = firstOrNull()?.indices.allRanges()
    return acceptableIndices.firstOrNull { indices ->
        all { row -> row.slice(indices).isPalindrome() }
    }?.reflectionPoint() ?: 0
}

private fun String.isPalindrome(): Boolean {
    for (i in 0..(length / 2)) {
        if (this[i] != this[length - i - 1]) return false
    }
    return true
}

private fun List<String>.isPalindrome(): Boolean {
    for (i in 0..(size / 2)) {
        if (this[i] != this[size - i - 1]) return false
    }
    return true
}

private fun IntRange.reflectionPoint() =
        if (!isEmpty()) (first + (last - first) / 2) + ((last - first) % 2) else 0

private fun IntRange?.allRanges() = this?.let { range ->
    buildSet {
        add(range)
        for (i in 1..(range.last - 1)) {
            add(0..(range.last - i))
            add(i..range.last)
        }
    }
} ?: emptySet()

private fun allChecks() {
    // String.isPalindrome
    check("##".isPalindrome())
    check("#.#".isPalindrome())
    check(!"#.".isPalindrome())
    check(!"#..".isPalindrome())

    // List<String>.isPalindrome
    check(listOf("#", "#").isPalindrome())
    check(listOf("#", ".", "#").isPalindrome())
    check(!listOf("#", ".").isPalindrome())
    check(!listOf("#", ".", ".").isPalindrome())

    // reflectionPoint
    checkValue(1, (0..1).reflectionPoint()) // ^##
    checkValue(1, (0..2).reflectionPoint()) // ^#.#
    checkValue(3, (2..3).reflectionPoint()) // ..##$
    checkValue(2, (1..3).reflectionPoint()) // .###$
    checkValue(4, (3..4).reflectionPoint()) // ...##$
    checkValue(3, (2..4).reflectionPoint()) // ..###$

    // allRanges
    checkValue(setOf(0..2, 0..1, 1..2), "#.#".indices.allRanges())
    checkValue(setOf(0..3, 0..2, 1..3, 0..1, 2..3), "#.#.".indices.allRanges())

    // findMirrorRow
    val testInput = listOf(
            "..#",
            "..#",
            "###",
    )
    checkValue(1, testInput.findMirrorRow())
}
