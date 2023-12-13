import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    fun part1(input: List<String>): Int {
        return input.map(String::toRecords).sumOf { (record, criteria) ->
            val permutations = findStart(record.toCharArray(), criteria.toIntArray())
            permutations.count()
        }
    }

    fun part2(input: List<String>): Int = runBlocking {
        input.map { it.toRecords().expand(times = 4) }
                .chunked(64)
                .map { chunked ->
                    async(Dispatchers.IO) {
                        chunked.sumOf { (record, criteria) ->
                            val permutations = findStart(record.toCharArray(), criteria.toIntArray())
                            permutations.count()
                        }
                    }
                }.awaitAll().sum()
    }

    checkValue(21, part1(readInput("Day12_test")))

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}

private fun String.toRecords(): Pair<String, List<Int>> {
    val parts = split(" ")
    val damagedConditions = parts[0]
    val groupSizes = parts[1].split(",").map(String::toInt)
    return damagedConditions to groupSizes
}

private fun Pair<String, List<Int>>.expand(times: Int): Pair<String, List<Int>> {
    val expandedRecord = List(times) { first }.joinToString("?")
    val expandedCriteria = List(second.count() * times) { second[it % second.count()] }
    return expandedRecord to expandedCriteria
}

private fun findStart(record: CharArray, criteria: IntArray): List<CharArray> {
    val remaining = criteria.sum() + (criteria.lastIndex)
    return if (record.size < remaining)
        emptyList()
    else if (criteria.isEmpty() && record.contains('#'))
        emptyList()
    else if (record.isEmpty() && criteria.isNotEmpty())
        emptyList()
    else if (record.isEmpty() || criteria.isEmpty())
        listOf(charArrayOf())
    else when (val c = record.first()) {
        '#' -> if (criteria.first() > 1)
            consumeGroup(record.copyOfRange(1, record.size), criteria.copyOf().apply { this[0]-- }).map { charArrayOf('#') + it }
        else
            endGroup(record.copyOfRange(1, record.size), criteria.copyOfRange(1, criteria.size)).map { charArrayOf('#') + it }
        '.' -> findStart(record.copyOfRange(1, record.size), criteria).map { charArrayOf('.') }
        '?' -> if (criteria.first() > 1)
            buildList {
                addAll(consumeGroup(record.copyOfRange(1, record.size), criteria.copyOf().apply { this[0]-- }).map { charArrayOf('#') + it })
                addAll(findStart(record.copyOfRange(1, record.size), criteria).map { charArrayOf('.') })
            }
        else
            buildList {
                addAll(endGroup(record.copyOfRange(1, record.size), criteria.copyOfRange(1, criteria.size)).map { charArrayOf('#') + it })
                addAll(findStart(record.copyOfRange(1, record.size), criteria).map { charArrayOf('.') + it })
            }
        else -> error("Received unexpected character: $c")
    }
}

private fun consumeGroup(record: CharArray, criteria: IntArray): List<CharArray> {
    val remaining = criteria.sum() + (criteria.lastIndex)
    return if (record.size < remaining)
        emptyList()
    else if (criteria.isEmpty() && record.contains('#'))
        emptyList()
    else if (record.isEmpty() && criteria.isNotEmpty())
        emptyList()
    else if (record.isEmpty() || criteria.isEmpty())
        listOf(charArrayOf())
    else when (val c = record.first()) {
        '#', '?' -> if (criteria.first() > 1)
            consumeGroup(record.copyOfRange(1, record.size), criteria.copyOf().apply { this[0]-- }).map { charArrayOf('#') + it }
        else
            endGroup(record.copyOfRange(1, record.size), criteria.copyOfRange(1, criteria.size)).map { charArrayOf('#') + it }
        '.' -> emptyList()
        else -> error("Received unexpected character: $c")
    }
}

private fun endGroup(record: CharArray, criteria: IntArray): List<CharArray> {
    val remaining = criteria.sum() + (criteria.lastIndex)
    return if (record.size < remaining)
        emptyList()
    else if (criteria.isEmpty() && record.contains('#'))
        emptyList()
    else if (record.isEmpty() && criteria.isNotEmpty())
        emptyList()
    else if (record.isEmpty() || criteria.isEmpty())
        listOf(charArrayOf())
    else when (val c = record.first()) {
        '.', '?' -> findStart(record.copyOfRange(1, record.size), criteria).map { charArrayOf('.') }
        '#' -> emptyList()
        else -> error("Received unexpected character: $c")
    }
}
