fun main() {
    fun part1(input: List<String>): Int {
        DigPlan(input).apply {
            "Offsets: x -> $xOffset, y -> $yOffset".println()
            "Dimensions: width -> $width, height -> $height".println()
            createString().println()
        }
        return Int.MAX_VALUE
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

//    allChecks(readInput("Day18_test"))

    val input = readInput("Day18")
    part1(input).println()
}

private class DigPlan(input: List<String>) {

    val records = input.map { line ->
        val tokens = line.split(" ")
        val direction = tokens[0].single()
        val distance = tokens[1].toInt()
        val vec = when (direction) {
            'U' -> Vector2D(0, -1)
            'R' -> Vector2D(1, 0)
            'D' -> Vector2D(0, 1)
            'L' -> Vector2D(-1, 0)
            else -> throw IllegalArgumentException("Unknown direction: $direction")
        }
        vec * distance
    }

    val geoMap: Map<Int, Set<Int>> = buildMap<Int, MutableList<Int>> {
        set(0, mutableListOf(0)) // initial hole
        var loc = Vector2D(0, 0)
        for (r in records) {
            loc += r
            val e = getOrPut(loc.y) { mutableListOf() }
            e.addLast(loc.x)
        }
    }.mapValues { (_, v) -> v.toSortedSet() }.toSortedMap()

    val xOffset = geoMap.values.minOf { it.first() }
    val yOffset: Int = geoMap.keys.first()
    val width = geoMap.values.maxOf { it.last() } - xOffset + 1
    val height = geoMap.keys.last() - yOffset + 1

    fun createString(): String {
        var prev = BooleanArray(width) { false }
        return (0..<height).joinToString(System.lineSeparator()) { y ->
            val trenches = geoMap.getOrDefault(y, emptySet())
            val curr = BooleanArray(width) { x -> (x in trenches) }
            val next = BooleanArray(width) { x -> curr[x] xor prev[x] }
            val combined = next.zip(curr).map { (n, c) -> n || c }
            prev = next
            var isTrench = false
            next.mapIndexed { index, b ->
                val res = isTrench || b
                isTrench = isTrench xor b
                res
            }.joinToString("") { if (it) "#" else "." }
        }
    }
}

private fun allChecks(testInput: List<String>) {
    DigPlan(testInput).apply {
        "Offsets: x -> $xOffset, y -> $yOffset".println()
        "Dimensions: width -> $width, height -> $height".println()
        createString().println()
    }
}