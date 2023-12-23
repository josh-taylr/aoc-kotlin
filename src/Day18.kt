import CompassPoints.*

fun main() {
    fun part1(input: List<String>) = DigPlan(input).calcVolume()

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    checkValue(62, part1(readInput("Day18_test")))

    val input = readInput("Day18")
    part1(input).println()
}

private class DigPlan(input: List<String>) {

    val records = parseRecords(input)

    val geoMap: Map<Int, Set<Int>> = buildMap<Int, MutableList<Int>> {
        set(0, mutableListOf(0)) // initial hole
        var loc = Vector2D(0, 0)
        for (r in records) {
            val next = loc + r
            val dir = (next - loc).normalise()
            while (loc != next) {
                loc += dir
                val e = getOrPut(loc.y) { mutableListOf() }
                e.addLast(loc.x)
            }
            loc = next
        }
    }.mapValues { (_, v) -> v.toSortedSet() }.toSortedMap()

    val xOffset = geoMap.values.minOf { it.first() }
    val yOffset: Int = geoMap.keys.first()
    val width = geoMap.values.maxOf { it.last() } - xOffset + 1
    val height = geoMap.keys.last() - yOffset + 1

    fun createString() = (0..<height).joinToString(System.lineSeparator()) { y ->
        val trenches = geoMap.getOrDefault(y + yOffset, emptySet())
        CharArray(width) { if (it + xOffset in trenches) '#' else '.' }.joinToString("")
    }

    fun calcVolume(): Int {
        val firstCorner = geoMap.entries.first().let { (y, xs) -> Point(xs.first() - xOffset, y - yOffset) }
        return createString().fill(firstCorner + SouthEast, '.', '#').count { it == '#' }
    }

    private fun parseRecords(input: List<String>): List<Vector2D> {
        return input.map { line ->
            val tokens = line.split(" ")
            val direction = tokens[0].single()
            val distance = tokens[1].toInt()
            val vec = when (direction) {
                'U' -> North
                'R' -> East
                'D' -> South
                'L' -> West
                else -> throw IllegalArgumentException("Unknown direction: $direction")
            }
            vec * distance
        }
    }
}