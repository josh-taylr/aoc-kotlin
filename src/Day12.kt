import kotlin.math.pow
import kotlinx.coroutines.*

fun main() {
    fun part1(input: List<String>): Int {
        return input.map(String::toRecords).sumOf { (record, criteria) ->
            var count = 0
            val unknown = record.getUnknown()
            // generate all permutations of record where unknown are set to either '#' or '.'
            for (i in 0 until 2.0.pow(unknown.size).toInt()) {
                val permutation =
                    unknown
                        .foldIndexed(record.toCharArray()) { idx, perm, pos ->
                            perm.apply { set(pos, if (i and (1 shl idx) != 0) '#' else '.') }
                        }
                        .concatToString()
                if (permutation.isFixed(criteria)) count++
            }
            count
        }
    }

    fun part2(input: List<String>): Int = runBlocking {
        input
            .map { it.toRecords().expand() }
            .mapIndexed { recordId, (record, criteria) ->
                async(CoroutineName("record-${record.take(10)}") + Dispatchers.IO) {
                    val unknown = record.getUnknown()
                    val total = (2.0.pow(unknown.size).toInt())
                    val tenth = total / 10
                    var nextPrint = tenth
                    var count = 0
                    var i = 0
                    // generate all permutations of record where unknown are set to either '#' or
                    // '.'
                    println("Record $recordId, Permutation: 0 / $total")
                    while (isActive && i < total) {
                        if (i > nextPrint) {
                            println("Record $recordId, Permutation: $i / $total")
                            nextPrint += tenth
                        }
                        val permutation =
                            unknown
                                .foldIndexed(record.toCharArray()) { idx, perm, pos ->
                                    perm.apply {
                                        set(pos, if (i and (1 shl idx) != 0) '#' else '.')
                                    }
                                }
                                .concatToString()
                        if (permutation.isFixed(criteria)) count++

                        i += 1
                    }
                    count
                }
            }
            .awaitAll()
            .sum()
    }

    checkValue(21, part1(readInput("Day12_test")))

    val input = readInput("Day12")
    part1(input).println()
    //    part2(input).println()
}

private fun String.toRecords(): Pair<String, List<Int>> {
    val parts = split(" ")
    val damagedConditions = parts[0]
    val groupSizes = parts[1].split(",").map(String::toInt)
    return damagedConditions to groupSizes
}

private fun Pair<String, List<Int>>.expand(): Pair<String, List<Int>> {
    val repetitions = 5
    val expandedRecord = first.repeat(repetitions)
    val expandedCriteria = List(second.count() * repetitions) { second[it % second.count()] }
    return expandedRecord to expandedCriteria
}

private fun String.getUnknown() =
    foldIndexed(emptyList<Int>()) { idx, unknown, c -> if (c == '?') unknown + idx else unknown }

private fun String.isFixed(criteria: List<Int>): Boolean {
    // build a regular expression for the criteria
    // e.g. criteria: 1,1,3 -> regex: /^[.]*[#]{1}[.]+[#]{1}[.]+[#]{3}[.]*$/
    val regex =
        criteria
            .joinToString(separator = "[.]+", prefix = "^[.]*", postfix = "[.]*\$") { groupSize ->
                "[#]{$groupSize}"
            }
            .toRegex()
    return matches(regex)
}
