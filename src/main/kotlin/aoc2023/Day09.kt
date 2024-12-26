package aoc2023

import AOCDay
import toIntList
import deltas

class Day09 : AOCDay(year = "2023", day = "9")  {
    override fun part1(input: List<String>) = input.map(String::toIntList).sumOf { it.extrapolate().last() }

    override fun part2(input: List<String>) = input.map(String::toIntList).sumOf { it.extrapolate().first() }
}

private fun List<Int>.extrapolate(): List<Int> =
    accumulateDeltas().reduceRight { numbers, deltas ->
        buildList {
            add(numbers.first() - deltas.first())
            addAll(numbers)
            add(numbers.last() + deltas.last())
        }
    }

private fun List<Int>.accumulateDeltas() =
    generateSequence(this) { it.deltas().takeUnless { deltas -> deltas.all { n -> n == 0 } } }
        .toList()
