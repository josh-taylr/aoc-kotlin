import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.toListD2

private const val CycleCount = 1_000_000_000L

fun main() {
    fun part1(input: List<String>): Int {
        val platform = Platform.parse(input)
        platform.moveLever(Platform.Tilt.North)
        return platform.calcLoad()
    }

    fun part2(input: List<String>): Int {
        val platform = Platform.parse(input)
        val seen = mutableMapOf<Platform, Long>()
        var i = 0L

        while (i < CycleCount) {
            if (platform in seen) {
                val cycleStart = seen[platform]!!
                val cycleLength = i - cycleStart
                val remainingSteps = (CycleCount - cycleStart) % cycleLength
                for (j in 0 until remainingSteps) {
                    platform.cycle()
                }
                return platform.calcLoad()
            }

            seen[platform] = i
            platform.cycle()
            i++
        }

        return platform.calcLoad()
    }

    allChecks(readInput("Day14_test"))

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}

class Platform(val spaces: D2Array<Int>) {

    fun moveLever(direction: Tilt) {
        when (direction) {
            Tilt.North -> {
                spaces.rotate90()
                do {
                    val didShift = rollRocks(spaces)
                } while (didShift)
                spaces.rotate270()
            }
            Tilt.East -> {
                do {
                    val didShift = rollRocks(spaces)
                } while (didShift)
            }
            Tilt.South -> {
                spaces.rotate270()
                do {
                    val didShift = rollRocks(spaces)
                } while (didShift)
                spaces.rotate90()
            }
            Tilt.West -> {
                spaces.rotate180()
                do {
                    val didShift = rollRocks(spaces)
                } while (didShift)
                spaces.rotate180()

            }
        }
    }

    fun cycle() {
        repeat(4) {
            spaces.rotate90()
            do {
                val didShift = rollRocks(spaces)
            } while (didShift)
        }
    }

    fun calcLoad(): Int =
        spaces.toListD2().let { spaces ->
            spaces.mapIndexed { idx, row ->
                val loadPerRoundSpace = spaces.size - idx
                val roundSpaces = row.count { space -> space == Round }
                (roundSpaces * loadPerRoundSpace)
            }
            .sum()
        }

    override fun toString(): String {
        return spaces.toListD2().joinToString(System.lineSeparator()) { row ->
            row.joinToString("") { space -> "${toChar(space)}" }
        }
    }

    override fun equals(other: Any?) =
        when (other) {
            is Platform -> this.spaces == other.spaces
            else -> false
        }

    override fun hashCode(): Int {
        return spaces.hashCode()
    }

    private fun rollRocks(grid: D2Array<Int>): Boolean {
        var didShift = false
        for (row in 0..< grid.shape[0]) {
            for (col in 0..<(grid.shape[1] -1)) {
                if (grid[row, col] == Round && grid[row, col + 1] == Empty) {
                    grid[row, col + 1] = Round
                    grid[row, col] = Empty
                    didShift = true
                }
            }
        }
        return didShift;
    }

    enum class Tilt {
        North,
        East,
        South,
        West
    }

    companion object {

        private const val Empty = 0
        private const val Round = 1
        private const val Cube = 2

        fun parse(input: List<String>): Platform {
            val matrix = input.map { str -> str.map(::toSpace) }
            return Platform(mk.ndarray(matrix))
        }

        private fun toSpace(c: Char) = when (c) {
            'O' -> Round
            '.' -> Empty
            '#' -> Cube
            else -> error("Unexpected char: $c")
        }

        private fun toChar(space: Int) = when (space) {
            Round -> 'O'
            Empty -> '.'
            Cube -> '#'
            else -> error("Unexpected space: $space")
        }
    }
}

private fun allChecks(testInput: List<String>) {

    val platform = Platform.parse(testInput)
    checkValue(
        """
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
    """
            .trimIndent(),
        platform.toString()
    )
    platform.moveLever(Platform.Tilt.North)
    checkValue(
        """
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
    """
            .trimIndent(),
        platform.toString()
    )
    checkValue(136, platform.calcLoad())
}

private fun D2Array<Int>.rotate90() {
    val n = shape[0]
    val m = shape[1]
    val copy = copy()

    for (i in 0 until n) {
        for (j in 0 until m) {
            this[j, n - i - 1] = copy[i, j]
        }
    }
}

private fun D2Array<Int>.rotate180() {
val n = shape[0]
    val m = shape[1]
    val copy = copy()

    for (i in 0 until n) {
        for (j in 0 until m) {
            this[n - i - 1, m - j - 1] = copy[i, j]
        }
    }
}

private fun D2Array<Int>.rotate270() {
    val n = shape[0]
    val m = shape[1]
    val copy = copy()

    for (i in 0 until n) {
        for (j in 0 until m) {
            this[n - i - 1, j] = copy[j, i]
        }
    }
}
