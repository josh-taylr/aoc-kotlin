fun main() {
    fun part1(input: List<String>): Int {
        return input.toPatterns().sumOf(List<String>::summarise)
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

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
    val acceptableRanges = indices.allRanges()
    return acceptableRanges.firstOrNull { range ->
        subList(range.first, range.last).isPalindrome()
    }?.reflectionPoint() ?: 0
}

private fun List<String>.findMirrorColumn(): Int {
    val acceptableRanges = firstOrNull()?.indices.allRanges()
    return acceptableRanges.firstOrNull { range ->
        all { row -> row.substring(range.first, range.last).isPalindrome() }
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
        if (!isEmpty()) first + (last - first) / 2 else 0

private fun IntRange?.allRanges() = this?.let { str ->
    buildSet {
        add(str)
        for (i in 1..<(str.count() - 3)) {
            add(0..(str.last - i))
            add(i..str.last)
        }
    }
} ?: emptySet()