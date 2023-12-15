import Platform.Space.*

fun main() {
    fun part1(input: List<String>): Int {
//        val platform = Platform.parse(input)
//        platform.moveLever(Platform.Tilt.North)
//        return platform.calcLoad()
        return Int.MAX_VALUE
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    allChecks(readInput("Day14_test"))

    val input = readInput("Day14")
    part1(input).println()
}

class Platform(val spaces: Array<Array<Space>>) {

    fun moveLever(direction: Tilt) {
        if (direction == Tilt.North) {
            spaces.tiltNorth()
        }
    }

    fun calcLoad(): Int = spaces.mapIndexed { idx, row ->
        val loadPerRoundSpace = spaces.size - idx
        val roundSpaces = row.count { space -> space == Round }
        (roundSpaces * loadPerRoundSpace).also { it.println() }
    }.sum()

    override fun toString(): String {
        return spaces.joinToString(System.lineSeparator()) { row ->
            row.joinToString("") { space -> "${space.char}" }
        }
    }

    override fun equals(other: Any?) = when (other) {
        is Platform -> this.spaces.contentDeepEquals(other.spaces)
        else -> false
    }

    override fun hashCode(): Int {
        return spaces.contentDeepHashCode()
    }

    private fun Array<Array<Space>>.tiltNorth() {
        for (col in this.first().indices) {
            // FIXME: sortBy performs a shallow copy :(
            val sortedByCol = sortedBy { row -> row[col] }
            for (row in this.indices) {
                this[row][col] = sortedByCol[row][col]
            }
        }
    }

    sealed class Space(val char: Char): Comparable<Space> {

        object Round : Space('O') {
            override fun compareTo(other: Space): Int = when(other) {
                Cube -> 0
                Empty -> -1
                Round -> 0
            }
        }

        object Empty : Space('.') {
            override fun compareTo(other: Space): Int = when(other) {
                Cube -> 0
                Empty -> 0
                Round -> 1
            }
        }

        object Cube : Space('#') {
            override fun compareTo(other: Space): Int = 0
        }

        companion object {
            fun toSpace(c: Char) = when (c) {
                'O' -> Round
                '.' -> Empty
                '#' -> Cube
                else -> error("Unexpected char: $c")
            }
        }
    }
    enum class Tilt { North, East, South, West }
    companion object {

        fun parse(input: List<String>): Platform {
            val matrix = input.map { str ->
                str.map(Space::toSpace).toTypedArray()
            }.toTypedArray()
            return Platform(matrix)
        }
    }
}

private fun allChecks(testInput: List<String>) {

    val platform = Platform.parse(testInput)
    checkValue("""
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent(), platform.toString())
    platform.moveLever(Platform.Tilt.North)
    checkValue("""
        OOOO.#.O..
        OO..#....#
        OO..O##..O
        O..#.OO...
        ........#.
        ..#....#.#
        ..O..#.O.O
        ..O.......
        #....###..
        #....#....
    """.trimIndent(), platform.toString())
    checkValue(136, platform.calcLoad())
}