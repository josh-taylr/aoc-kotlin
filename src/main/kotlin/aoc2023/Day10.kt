package aoc2023

import AOCDay
import Point
import kotlin.math.nextUp
import kotlin.math.roundToInt

class Day10 : AOCDay(year = "2023", day = "10")  {
    override fun part1(input: List<String>) = Maze.parse(input).maxDistance

    override fun part2(input: List<String>) = Maze.parse(input).enclosed
}

data class Maze(private val field: List<List<Pipe>>, private val start: Point) {

    init {
        mapLoop()
        mapInside()
    }

    val maxDistance =
        field
            .flatten()
            .maxOf { it.distanceFromStart ?: 0 }
            .let { ((it).toFloat() / 2).nextUp().roundToInt() }

    val enclosed = field.flatten().count { it.isInside }

    private fun mapLoop() {
        val visited = ArrayDeque(listOf(start))
        start.let { (row, col) -> field[row][col].distanceFromStart = 0 }
        val new = neighbours(start).first()
        visited.addLast(new)
        var count = 0
        while (visited.lastOrNull() != start) {
            val next = visited.lastOrNull() ?: start
            val neighbours = neighbours(next).filterNot { it in visited.takeLast(2) }.first()
            next.let { (row, col) -> field[row][col].distanceFromStart = ++count }
            visited.addLast(neighbours)
        }
    }

    private fun neighbours(point: Point): List<Point> {
        val (row, col) = point
        return when (field[row][col]) {
            is Pipe.Vertical -> listOf(Point(row - 1, col), Point(row + 1, col))
            is Pipe.Horizontal -> listOf(Point(row, col - 1), Point(row, col + 1))
            is Pipe.NorthToEast -> listOf(Point(row - 1, col), Point(row, col + 1))
            is Pipe.NorthToWest -> listOf(Point(row - 1, col), Point(row, col - 1))
            is Pipe.SouthToWest -> listOf(Point(row + 1, col), Point(row, col - 1))
            is Pipe.SouthToEast -> listOf(Point(row + 1, col), Point(row, col + 1))
            is Pipe.Ground -> emptyList()
        }
    }

    private fun mapInside() {
        field.forEach { row ->
            var state: ScanState = ScanState.Search()
            row.forEach { pipe ->
                if (pipe.distanceFromStart != null) {
                    state += pipe
                } else {
                    pipe.isInside = state.inside
                }
            }
        }
    }

    private sealed class ScanState(val inside: Boolean) {

        abstract operator fun plus(pipe: Pipe): ScanState

        class Search(inside: Boolean = false) : ScanState(inside) {
            override fun plus(pipe: Pipe) =
                when (pipe) {
                    is Pipe.Vertical -> Search(!inside)
                    is Pipe.WestCorner -> Curve(pipe, inside)
                    else -> this
                }
        }

        class Curve(private val start: Pipe.WestCorner, inside: Boolean) : ScanState(inside) {
            override fun plus(pipe: Pipe) =
                when (start) {
                    is Pipe.NorthToEast ->
                        when (pipe) {
                            is Pipe.NorthToWest -> Search(inside)
                            is Pipe.SouthToWest -> Search(!inside)
                            is Pipe.Horizontal -> this
                            else -> error("Invalid pipe: $pipe")
                        }
                    is Pipe.SouthToEast ->
                        when (pipe) {
                            is Pipe.NorthToWest -> Search(!inside)
                            is Pipe.SouthToWest -> Search(inside)
                            is Pipe.Horizontal -> this
                            else -> error("Invalid pipe: $pipe")
                        }
                }
        }
    }

    companion object {
        fun parse(input: List<String>): Maze {
            var start: Point? = null
            val field: List<List<Pipe>> =
                List(input.count()) { row ->
                    List(input[row].count()) { col ->
                        val c = input[row][col]
                        if (c == 'S') {
                            start = Point(row, col)
                            validPipe(input, row, col)
                        } else {
                            Pipe.parse(c)
                        }
                    }
                }

            return Maze(field, requireNotNull(start) { "Input without start symbol." })
        }

        private fun validPipe(input: List<String>, row: Int, col: Int) =
            when {
                hasUp(input, row, col) && hasDown(input, row, col) -> Pipe.Vertical()
                hasLeft(input, row, col) && hasRight(input, row, col) -> Pipe.Horizontal()
                hasUp(input, row, col) && hasRight(input, row, col) -> Pipe.NorthToEast()
                hasUp(input, row, col) && hasLeft(input, row, col) -> Pipe.NorthToWest()
                hasDown(input, row, col) && hasLeft(input, row, col) -> Pipe.SouthToWest()
                hasDown(input, row, col) && hasRight(input, row, col) -> Pipe.SouthToEast()
                else -> error("Invalid pipe at $row, $col")
            }

        private fun hasUp(field: List<String>, row: Int, col: Int) =
            row > 0 && field[row - 1][col] in setOf('|', '7', 'F')

        private fun hasRight(field: List<String>, row: Int, col: Int) =
            col < field[row].lastIndex && field[row][col + 1] in setOf('-', '7', 'J')

        private fun hasDown(field: List<String>, row: Int, col: Int) =
            row < field.lastIndex && field[row + 1][col] in setOf('|', 'L', 'J')

        private fun hasLeft(field: List<String>, row: Int, col: Int) =
            col > 0 && field[row][col - 1] in setOf('-', 'L', 'F')
    }

    sealed class Pipe {

        var isInside: Boolean = false

        var distanceFromStart: Int? = null

        sealed interface WestCorner

        class Vertical : Pipe()

        class Horizontal : Pipe()

        class NorthToEast : Pipe(), WestCorner

        class NorthToWest : Pipe()

        class SouthToWest : Pipe()

        class SouthToEast : Pipe(), WestCorner

        class Ground : Pipe()

        companion object {
            fun parse(c: Char): Pipe =
                when (c) {
                    '|' -> Vertical()
                    '-' -> Horizontal()
                    'L' -> NorthToEast()
                    'J' -> NorthToWest()
                    '7' -> SouthToWest()
                    'F' -> SouthToEast()
                    '.' -> Ground()
                    else -> error("Invalid Pipe character: '$c'")
                }
        }
    }
}
