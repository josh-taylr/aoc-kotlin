package aoc2024

import AOCDay

class Day02 : AOCDay(year = "2024", day = "2") {
    override fun part1(input: List<String>): Int {
        return input.asSequence()
            .map { line -> line.split(" ").map { it.trim() }.map { it.toInt() } }
            .count(::isValidSequence)
    }

    override fun part2(input: List<String>): Int {
        return input.asSequence()
            .map { line -> line.split(" ").map { it.trim() }.map { it.toInt() } }
            .count(::isValidWithRemoval)
    }

    private fun isValidSequence(numbers: List<Int>): Boolean {
        if (numbers.size < 2) return true
        
        val firstDelta = numbers[1] - numbers[0]
        val expectedRange = if (firstDelta > 0) 1..3 else -3..-1
        
        return numbers.asSequence()
            .zipWithNext { a, b -> b - a }
            .all { it in expectedRange }
    }

    private fun isValidWithRemoval(numbers: List<Int>): Boolean {
        // Quick check for already valid sequence
        if (isValidSequence(numbers)) return true
        
        // Early exit for sequences too short after removal
        if (numbers.size <= 2) return true
        
        // Try removing each number and check if the resulting sequence is valid
        return numbers.indices.any { skipIndex ->
            val modified = buildList(numbers.size - 1) {
                for (i in numbers.indices) {
                    if (i != skipIndex) add(numbers[i])
                }
            }
            isValidSequence(modified)
        }
    }
}