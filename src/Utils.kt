import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

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

fun List<Int>.deltas() = this.zipWithNext { a, b -> b - a }
