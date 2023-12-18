import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.*

/** Reads lines from the given input txt file. */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/** Converts string to md5 hash. */
fun String.md5() =
    BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
        .toString(16)
        .padStart(32, '0')

/** The cleaner shorthand for printing output. */
fun Any?.println() = println(this)

fun <T> checkValue(expected: T, actual: T) {
    check(expected == actual) { "expected: $expected, actual: $actual" }
}

fun String.toIntList(): List<Int> = this.trim().split("\\s+".toPattern()).map(String::toInt)

fun String.toUIntList(): List<UInt> = this.trim().split("\\s+".toPattern()).map(String::toUInt)

fun String.toLongList(): List<Long> = this.trim().split("\\s+".toPattern()).map(String::toLong)

fun CharSequence.asWrappingSequence(): Sequence<Char> = sequence {
    var i = 0
    do {
        yield(this@asWrappingSequence[i])
        i = (i + 1) % this@asWrappingSequence.count()
    } while (true)
}

inline fun <reified T> Array<Array<T>>.deepCopy(): Array<Array<T>> {
    return Array(size) { row -> Array(this[row].size) { col -> this[row][col] } }
}

fun List<Int>.deltas() = this.zipWithNext { a, b -> b - a }

data class Point(val x: Int, val y: Int)

data class Vector2D(var x: Int, var y: Int) {
    operator fun plus(v: Vector2D) = Vector2D(x + v.x, y + v.y)
    operator fun minus(v: Vector2D) = Vector2D(x - v.x, y - v.y)
    operator fun times(scalar: Int) = Vector2D(x * scalar, y * scalar)
    operator fun div(scalar: Int) = Vector2D(x / scalar, y / scalar)
    fun inRanges(xRange: IntRange, yRange: IntRange) = x in xRange && y in yRange
    fun coerceIn(xRange: IntRange, yRange: IntRange) = Vector2D(x.coerceIn(xRange), y.coerceIn(yRange))
    fun distance(v: Vector2D) = sqrt((x - v.x).toDouble().pow(2) + (y - v.y).toDouble().pow(2))
    fun manhattanDistance(v: Vector2D) = abs(x - v.x) + abs(y - v.y)

    fun rotate(degrees: Double): Vector2D {
        val radians = Math.toRadians(degrees)
        val cos = cos(radians)
        val sin = sin(radians)

        val nx = x * cos - y * sin
        val ny = x * sin + y * cos

        return Vector2D(nx.toInt(), ny.toInt())
    }
}

fun <T> aStar(
        start: T,
        goal: (T) -> Boolean,
        heuristic: (T) -> Double,
        neighbors: (T) -> Iterable<T>,
        weight: (current: T, neighbour: T) -> Double
): List<T>? {

    fun <T> reconstructPath(preceding: MutableMap<T, T>, current: T): List<T> {
        val totalPath = mutableListOf(current)
        var currentTemp = current
        while (currentTemp in preceding.keys) {
            currentTemp = preceding[currentTemp]!!
            totalPath.add(0, currentTemp)
        }
        return totalPath
    }

    val minCosts = mutableMapOf<T, Double>().withDefault { Double.POSITIVE_INFINITY }
    minCosts[start] = 0.0

    val bestGuess = mutableMapOf<T, Double>().withDefault { Double.POSITIVE_INFINITY }
    bestGuess[start] = heuristic(start)

    val discovered = mutableSetOf(start)
    val preceding = mutableMapOf<T, T>()

    while (discovered.isNotEmpty()) {
        val current = discovered.minByOrNull{ bestGuess.getValue(it) } ?: error("")
        if (goal(current)) {
            return reconstructPath(preceding, current)
        }

        discovered.remove(current)
        for (neighbor in neighbors(current)) {
            val tentative = minCosts.getValue(current) + weight(current, neighbor)
            if (tentative < minCosts.getValue(neighbor)) {
                preceding[neighbor] = current
                minCosts[neighbor] = tentative
                bestGuess[neighbor] = tentative + heuristic(neighbor)
                if (neighbor !in discovered) {
                    discovered.add(neighbor)
                }
            }
        }
    }
    return null
}
