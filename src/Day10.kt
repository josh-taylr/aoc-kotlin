import Maze.Pipe.*
import kotlin.math.nextUp
import kotlin.math.roundToInt

fun main() {
    fun part1(input: List<String>): Int {
        return Maze.parse(input).run {
            loopSize(start)
        }
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    checkValue(4, part1(readInput("Day10_test")))
    checkValue(8, part1(readInput("Day10_test2")))

    val input = readInput("Day10")
    part1(input).println()
}

data class Maze(val field: List<List<Pipe>>, val start: Pair<Int, Int>) {
    fun loopSize(start: Pair<Int, Int>): Int {
        val visited = ArrayDeque<Pair<Int, Int>>(listOf(start))
        val (row, col) = start
        val new = neighbours(row, col).first()
        visited.addLast(new)
        while(visited.lastOrNull() != start) {
            val (row, col) = visited.lastOrNull() ?: start
            val new = neighbours(row, col).filterNot { it in visited.takeLast(2) }.first()
            visited.addLast(new)
        }
        return ((visited.count() - 1).toFloat() / 2).nextUp().roundToInt()
    }

    private fun neighbours(row: Int, col: Int): List<Pair<Int, Int>> {
        return when (getPipe(row, col)) {
            V -> listOf(row - 1 to col, row + 1 to col)
            H -> listOf(row to col - 1, row to col + 1)
            NE -> listOf(row - 1 to col, row to col + 1)
            NW -> listOf(row - 1 to col, row to col - 1)
            SW -> listOf(row + 1 to col, row to col - 1)
            SE -> listOf(row + 1 to col, row to col + 1)
            else -> emptyList()
        }
    }

    private fun getPipe(row: Int, col: Int) =
            field[row][col].takeUnless(Pipe::isStart) ?: validPipes(row, col).first()

    private fun validPipes(row: Int, col: Int): List<Pipe> {
        return buildList {
            if (hasUp(row, col) && hasDown(row, col)) add(V)
            if (hasLeft(row, col) && hasRight(row, col)) add(H)
            if (hasUp(row, col) && hasRight(row, col)) add(NE)
            if (hasUp(row, col) && hasLeft(row, col)) add(NW)
            if (hasDown(row, col) && hasLeft(row, col)) add(SW)
            if (hasDown(row, col) && hasRight(row, col)) add(SE)
        }
    }

    private fun hasUp(row: Int, col: Int) =
            row > 0 &&  field[row - 1][col] in listOf(V,SW, SE)
    private fun hasRight(row: Int, col: Int) =
            col < field[row].lastIndex && field[row][col + 1] in listOf(H, NW, SW)
    private fun hasDown(row: Int, col: Int) =
            row < field.lastIndex && field[row + 1][col] in listOf(V, NW, NE)
    private fun hasLeft(row: Int, col: Int) =
            col > 0 && field[row][col - 1] in listOf(H, NE, SE)

    companion object {
        fun parse(input: List<String>): Maze {
            var start: Pair<Int, Int>? = null
            val field: List<List<Pipe>> = List(input.count()) { i ->
                List(input[i].count()) { j ->
                    val pipe = Pipe.parse(input[i][j])
                    if (pipe == S) start = i to j
                    pipe
                }
            }
            return Maze(field, start!!)
        }
    }

    enum class Pipe {
        V, H, NE, NW, SW, SE, G, S;

        companion object {
            fun parse(c: Char): Pipe = when (c) {
                '|' -> V
                '-' -> H
                'L' -> NE
                'J' -> NW
                '7' -> SW
                'F' -> SE
                '.' -> G
                'S' -> S
                else -> error("Invalid Pipe character: '$c'")
            }

            fun isStart (it: Pipe) = it == S
        }
    }
}
