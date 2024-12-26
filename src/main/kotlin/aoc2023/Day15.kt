package aoc2023

import AOCDay

class Day15 : AOCDay(year = "2023", day = "15")  {
    override fun part1(input: List<String>) = input.single().split(',').sumOf { Step.parse(it).hashCode() }

    override fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }
}

class Step(private val instruction: String) {

    override fun equals(other: Any?) =
        when (other) {
            is Step -> this.hashCode() == other.hashCode()
            else -> false
        }

    override fun hashCode(): Int =
        instruction.fold(0) { current, c -> ((current + c.code) * 17) % 256 }

    companion object {
        fun parse(input: String) = Step(input)
    }
}
