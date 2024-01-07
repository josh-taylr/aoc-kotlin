fun main() {
    fun part1(input: List<String>) = input.single().split(',').sumOf { Step.parse(it).hashCode() }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    checkValue(1320, part1(readInput("Day15_test")))

    val input = readInput("Day15")
    part1(input).println()
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
