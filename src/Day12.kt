import java.lang.Integer.sum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    fun part1(input: List<String>): Int =
        input.map(String::toRecords).sumOf { (damaged, criteria) ->
            damaged.countPossibleArrangements(criteria)
        }

    fun part2(input: List<String>): Int = runBlocking {
        input.map { it.toRecords().expand(times = 4) }
            .chunked(32)
            .map { chunked ->
                async(Dispatchers.IO) {
                    chunked.sumOf { (damaged, criteria) ->
                        damaged.countPossibleArrangements(criteria)
                    }
                }
            }
            .awaitAll()
            .sum()
    }

    checkValue(21, part1(readInput("Day12_test")))

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}

private fun String.toRecords(): Pair<String, List<Int>> {
    val parts = split(" ")
    val record = parts[0]
    val criteria = parts[1].split(",").map(String::toInt)
    return record to criteria
}

private fun Pair<String, List<Int>>.expand(times: Int): Pair<String, List<Int>> {
    val expandedRecord = List(times) { first }.joinToString("?")
    val expandedCriteria = List(second.count() * times) { second[it % second.count()] }
    return expandedRecord to expandedCriteria
}

private fun String.countPossibleArrangements(criteria: List<Int>): Int =
    findStart(
        record = toCharArray(),
        recordStart = 0,
        criteria = criteria.toIntArray(),
        criteriaStart = 0
    )

private const val ImpossibleRecord = 0
private const val FixedRecord = 1

private fun findStart(
    record: CharArray,
    recordStart: Int,
    criteria: IntArray,
    criteriaStart: Int
): Int = when {
    isNotPossible(record, recordStart, criteria, criteriaStart) -> ImpossibleRecord
    isFixed(record, recordStart, criteria, criteriaStart) -> {
        // record.concatToString().println()
        FixedRecord
    }
    else -> {
        val c = record[recordStart]
        sum(
                if (c == '#' || c == '?') criteria.runWithDecrement(criteriaStart) { decrementedCriteria ->
                    record[recordStart] = '#'
                    val res = if (decrementedCriteria[criteriaStart] > 0)
                        consumeGroup(record, recordStart.inc(), decrementedCriteria, criteriaStart)
                    else
                        endGroup(record, recordStart.inc(), decrementedCriteria, criteriaStart.inc())
                    record[recordStart] = c
                    res
                } else 0,

                if (c == '.' || c == '?') {
                    record[recordStart] = '.'
                    val res = findStart(record, recordStart.inc(), criteria, criteriaStart)
                    record[recordStart] = c
                    res
                } else 0
        )
    }
}

private fun consumeGroup(
    record: CharArray,
    recordStart: Int,
    criteria: IntArray,
    criteriaStart: Int
): Int = when {
    isNotPossible(record, recordStart, criteria, criteriaStart) -> ImpossibleRecord
    isFixed(record, recordStart, criteria, criteriaStart) -> {
        // record.concatToString().println()
        FixedRecord
    }
    else -> {
        val c = record[recordStart]
        if (c == '#' || c == '?') criteria.runWithDecrement(criteriaStart) { decrementedCriteria ->
            record[recordStart] = '#'
            val res = if (decrementedCriteria[criteriaStart] > 0)
                consumeGroup(record, recordStart.inc(), decrementedCriteria, criteriaStart)
            else
                endGroup(record, recordStart.inc(), decrementedCriteria, criteriaStart.inc())
            record[recordStart] = c
            res
        } else
            ImpossibleRecord
    }
}

private fun endGroup(
    record: CharArray,
    recordStart: Int,
    criteria: IntArray,
    criteriaStart: Int
): Int = when {
    isNotPossible(record, recordStart, criteria, criteriaStart) -> ImpossibleRecord
    isFixed(record, recordStart.inc(), criteria, criteriaStart) -> {
        // record.concatToString().println()
        FixedRecord
    }
    else -> {
        val c = record[recordStart]
        if (c == '.' || c == '?') {
            record[recordStart] = '.'
            val res = if (criteria.last() > 0)
                findStart(record, recordStart.inc(), criteria, criteriaStart)
            else
                findEnd(record, recordStart.inc(), criteria, criteriaStart)
            record[recordStart] = c
            res
        } else
            ImpossibleRecord
    }
}

private fun findEnd(
        record: CharArray,
        recordStart: Int,
        criteria: IntArray,
        criteriaStart: Int
): Int = when {
    isNotPossible(record, recordStart, criteria, criteriaStart) -> ImpossibleRecord
    isFixed(record, recordStart.inc(), criteria, criteriaStart) -> {
        // record.concatToString().println()
        FixedRecord
    }
    else -> {
        val c = record[recordStart]
        if (c == '.' || c == '?') {
            record[recordStart] = '.'
            val res = findEnd(record, recordStart.inc(), criteria, criteriaStart)
            record[recordStart] = c
            res
        } else
            ImpossibleRecord
    }
}

private fun isNotPossible(
    record: CharArray,
    recordStart: Int,
    criteria: IntArray,
    criteriaStart: Int
): Boolean {
    val hasConsumedRecord = recordStart > record.lastIndex
    val hasConsumedCriteria = criteriaStart > criteria.lastIndex
    return hasConsumedRecord && !hasConsumedCriteria
}

private fun isFixed(
    record: CharArray,
    recordStart: Int,
    criteria: IntArray,
    criteriaStart: Int
): Boolean {
    val hasConsumedRecord = recordStart > record.lastIndex
    val hasConsumedCriteria = criteriaStart > criteria.lastIndex
    return hasConsumedRecord && hasConsumedCriteria
}

private inline fun <R> IntArray.runWithDecrement(index: Int = 0, action: (IntArray) -> R): R {
    this[index] -= 1
    val result = action(this)
    this[index] += 1
    return result
}
