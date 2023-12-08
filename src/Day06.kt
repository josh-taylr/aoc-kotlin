fun main() {
    fun part1(input: List<String>): Int {
        val times = input[0].substringAfter("Time:").toIntList()
        val records = input[1].substringAfter("Distance:").toIntList()
        val winningStrats =
            times.zip(records).map { (time, record) ->
                val strats =
                    (1 until time) // values 0 and 'time' == 0
                        .map { hold -> hold * (time - hold) }
                strats.filter { dist -> dist > record } // winning strategies
            }
        return winningStrats.map(List<Int>::count).fold(1) { acc, count -> acc * count }
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    check(part1(readInput("Day06_test")) == 288)

    val input = readInput("Day06")
    part1(input).println()
}
