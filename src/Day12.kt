import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    fun part1(input: List<String>): Long =
        input.map(String::toRecords).sumOf { (damaged, criteria) ->
            damaged.countPossibleArrangements(criteria)
        }

    fun part2(input: List<String>): Long = runBlocking {
        input
            .map { it.toRecords().expand(times = 5) }
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

private const val ImpossibleRecord = 0L
private const val FixedRecord = 1L
private val cache = mutableMapOf<String, Long>()

private fun CharSequence.countPossibleArrangements(criteria: List<Int>): Long {
    val compressed = this.replace("\\.+".toRegex(), ".")
    return findStart(
        record = compressed.toCharArray(),
        recordStart = 0,
        criteria = criteria.toIntArray(),
        criteriaStart = 0
    )
}

private fun findStart(record: CharArray, recordStart: Int, criteria: IntArray, criteriaStart: Int) =
    cache.getOrPut(createKey(record, recordStart, criteria, criteriaStart)) {
        val countWithBroken =
            when (record.getOrNull(recordStart)) {
                '#',
                '?' -> assumeBroken(criteria, criteriaStart, record, recordStart)
                else -> 0
            }
        val countWithOperational =
            when (record.getOrNull(recordStart)) {
                '.',
                '?' -> assumeOperational(record, recordStart, criteria, criteriaStart)
                else -> 0
            }

        if (countWithBroken > Long.MAX_VALUE - countWithOperational) error("Integer overflow")
        countWithBroken + countWithOperational
    }

private fun createKey(record: CharArray, recordStart: Int, criteria: IntArray, criteriaStart: Int) =
    "${record.concatToString(startIndex = recordStart)} ${criteria.drop(criteriaStart)}"

private fun consumeGroup(
    record: CharArray,
    recordStart: Int,
    criteria: IntArray,
    criteriaStart: Int
): Long =
    when (record.getOrNull(recordStart)) {
        '#',
        '?' -> assumeBroken(criteria, criteriaStart, record, recordStart)
        null -> endGroup(record, recordStart, criteria, criteriaStart)
        else -> ImpossibleRecord
    }

private fun endGroup(
    record: CharArray,
    recordStart: Int,
    criteria: IntArray,
    criteriaStart: Int
): Long =
    when (record.getOrNull(recordStart)) {
        '.',
        '?' -> assumeOperational(record, recordStart, criteria, criteriaStart)
        null -> if (criteria.last() > 0) ImpossibleRecord else FixedRecord
        else -> ImpossibleRecord
    }

private fun findEnd(
    record: CharArray,
    recordStart: Int,
    criteria: IntArray,
    criteriaStart: Int
): Long =
    when (record.getOrNull(recordStart)) {
        '.',
        '?' -> assumeOperational(record, recordStart, criteria, criteriaStart)
        null -> FixedRecord
        else -> ImpossibleRecord
    }

private fun assumeOperational(
    record: CharArray,
    recordStart: Int,
    criteria: IntArray,
    criteriaStart: Int
): Long {
    var res: Long
    record[recordStart] =
        record[recordStart].also {
            record[recordStart] = '.'
            res =
                if (criteria.last() > 0)
                    findStart(record, recordStart.inc(), criteria, criteriaStart)
                else findEnd(record, recordStart.inc(), criteria, criteriaStart)
        }
    return res
}

private fun assumeBroken(
    criteria: IntArray,
    criteriaStart: Int,
    record: CharArray,
    recordStart: Int
): Long =
    criteria.runWithDecrement(criteriaStart) { decrementedCriteria ->
        var res: Long
        record[recordStart] =
            record[recordStart].also {
                record[recordStart] = '#'
                res =
                    if (decrementedCriteria[criteriaStart] > 0)
                        consumeGroup(record, recordStart.inc(), decrementedCriteria, criteriaStart)
                    else
                        endGroup(
                            record,
                            recordStart.inc(),
                            decrementedCriteria,
                            criteriaStart.inc()
                        )
            }
        res
    }

private inline fun <R> IntArray.runWithDecrement(index: Int = 0, action: (IntArray) -> R): R {
    this[index] -= 1
    val result = action(this)
    this[index] += 1
    return result
}
