package aoc2023

import AOCDay

class Day16 : AOCDay(year = "2023", day = "16")  {
    override fun part1(input: List<String>): Int {
        val contraption = Contraption(input)
        while (contraption.hasActiveBeams) {
            contraption.moveForward()
        }
        return contraption.countEnergised()
    }

    override fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }
}

private class Contraption(
    input: List<String>,
    startRow: Int = 0,
    startCol: Int = -1,
) {

    private val grid: Array<Array<Tile>> =
        Array(input.size) { row -> Array(input[row].length) { col -> Tile.parse(input[row][col]) } }
    private val beams: ArrayDeque<LightBeam> =
        ArrayDeque(setOf(LightBeam.Right(startRow, startCol)))

    val hasActiveBeams: Boolean
        get() = beams.isNotEmpty()

    fun moveForward(): Boolean {
        if (!hasActiveBeams) return false
        repeat(beams.size) {
            val newBeams = beams.removeFirst().update()
            beams.addAll(newBeams)
        }
        return true
    }

    fun countEnergised(): Int {
        return grid.sumOf { row -> row.count { tile -> tile.temperature == Temp.Hot } }
    }

    fun heapMap(): String =
        grid.joinToString(System.lineSeparator()) { row ->
            row.joinToString("") { tile ->
                when (tile.temperature) {
                    Temp.Hot -> "#"
                    Temp.Cold -> "."
                }
            }
        }

    override fun toString(): String =
        grid.joinToString(System.lineSeparator()) { row -> row.joinToString("") }

    private fun LightBeam.update(): Set<LightBeam> {
        // move the light beam forward one tile
        val moved = move()
        if (isOffGrid(moved)) return emptySet()

        // adjust the direction of the new beam
        val tile = grid[moved.row][moved.col]
        val adjustedBeams = tile.adjust(beam = moved)
        tile.temperature = Temp.Hot

        return adjustedBeams
    }

    fun isOffGrid(beam: LightBeam): Boolean {
        // check row and column indexes are inside grid bounds
        return beam.row !in grid.indices || beam.col !in grid.first().indices
    }

    private sealed class Tile {
        abstract fun adjust(beam: LightBeam): Set<LightBeam>

        var temperature = Temp.Cold

        class Empty : Tile() {
            private val observations = mutableListOf<String>()

            override fun adjust(beam: LightBeam): Set<LightBeam> {
                observations += "$beam"
                return setOf(beam)
            }

            override fun toString() =
                when {
                    observations.isEmpty() -> "."
                    observations.count() == 1 -> observations.single()
                    else -> "${observations.count()}"
                }
        }

        class Mirror(private val angle: Char) : Tile() {
            override fun adjust(beam: LightBeam) =
                when (angle) {
                    '/' ->
                        when (beam) {
                            is LightBeam.Up -> setOf(beam.toRight())
                            is LightBeam.Right -> setOf(beam.toUp())
                            is LightBeam.Down -> setOf(beam.toLeft())
                            is LightBeam.Left -> setOf(beam.toDown())
                        }
                    '\\' ->
                        when (beam) {
                            is LightBeam.Up -> setOf(beam.toLeft())
                            is LightBeam.Right -> setOf(beam.toDown())
                            is LightBeam.Down -> setOf(beam.toRight())
                            is LightBeam.Left -> setOf(beam.toUp())
                        }
                    else -> error("Encountered invalid character: $angle")
                }

            override fun toString() = "$angle"
        }

        class Splitter(private val angle: Char) : Tile() {
            private val occurrences = mutableListOf<LightBeam>()

            override fun adjust(beam: LightBeam) =
                when (angle) {
                        '|' ->
                            when (beam) {
                                is LightBeam.Up,
                                is LightBeam.Down -> setOf(beam)
                                is LightBeam.Left,
                                is LightBeam.Right -> setOf(beam.toUp(), beam.toDown())
                            }
                        '-' ->
                            when (beam) {
                                is LightBeam.Up,
                                is LightBeam.Down -> setOf(beam.toLeft(), beam.toRight())
                                is LightBeam.Left,
                                is LightBeam.Right -> setOf(beam)
                            }
                        else -> error("Encountered invalid character: $angle")
                    }
                    .filterNot(occurrences::contains)
                    .also(occurrences::addAll)
                    .toSet()

            override fun toString() = "$angle"
        }

        companion object {
            fun parse(c: Char): Tile =
                when (c) {
                    '.' -> Empty()
                    '/',
                    '\\' -> Mirror(c)
                    '|',
                    '-' -> Splitter(c)
                    else -> error("Encountered invalid character: $c")
                }
        }
    }

    internal sealed class LightBeam(val row: Int, val col: Int) {

        abstract fun move(): LightBeam

        fun toRight() = Right(row, col)

        fun toUp() = Up(row, col)

        fun toLeft() = Left(row, col)

        fun toDown() = Down(row, col)

        class Up(row: Int, col: Int) : LightBeam(row, col) {
            override fun move() = Up(row.dec(), col)

            override fun toString() = "^"
        }

        class Left(row: Int, col: Int) : LightBeam(row, col) {
            override fun move() = Left(row, col.dec())

            override fun toString() = "<"
        }

        class Down(row: Int, col: Int) : LightBeam(row, col) {
            override fun move() = Down(row.inc(), col)

            override fun toString() = "v"
        }

        class Right(row: Int, col: Int) : LightBeam(row, col) {
            override fun move() = Right(row, col.inc())

            override fun toString() = ">"
        }

        override fun equals(other: Any?): Boolean {
            return when (other) {
                is LightBeam -> this.hashCode() == other.hashCode()
                else -> false
            }
        }

        override fun hashCode(): Int {
            val type =
                when (this) {
                    is Up -> 0
                    is Right -> 1
                    is Down -> 2
                    is Left -> 3
                }
            return 17 * type + 31 * row + 53 * col
        }
    }

    enum class Temp {
        Hot,
        Cold
    }
}
