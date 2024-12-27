package aoc2024

import AOCDay
import toIntList
import kotlin.math.abs

class Day01 : AOCDay(year = "2024", day = "1") {
    override fun part1(input: List<String>): Int {
        
        val pairs = input.map { line -> 
            line.split("   ").map { it.trim().toInt() }
        }
        
        val left = pairs.map { it[0] }.sorted()
        val right = pairs.map { it[1] }.sorted()
        
        return left.zip(right) { a, b -> abs(a - b) }.sum()
    }

    override fun part2(input: List<String>): Int {

        val pairs = input.map { line -> 
            line.split("   ").map { it.trim().toInt() }
        }

        val left = pairs.map { it[0] }
        val right = pairs.map { it[1] }

        return left.sumOf { a -> a * right.count { b -> a == b } }
    }
}