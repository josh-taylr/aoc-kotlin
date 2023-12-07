fun main() {
    fun part1(input: List<String>): Int {
        fun calcPoints(card: Card): Int {
            if (card.matchingNumbers.isEmpty()) return 0
            return card.matchingNumbers
                .drop(1) // first match is 1 point
                .fold(1) { acc, _ ->
                    acc * 2 // each match after the first doubles point value
                }
        }
        return input.sumOf { str ->
            val card = Card.parse(str)
            calcPoints(card)
        }
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    check(part1(readInput("Day04_test")) == 13)

    val input = readInput("Day04")
    part1(input).println()
}

data class Card(
    val cardNumber: Int,
    val winningNumbers: List<Int>,
    val myNumbers: List<Int>
) {
    val matchingNumbers: List<Int> by lazy {
        myNumbers.filter { num -> num in winningNumbers }
    }
    companion object {
        fun parse(input: String): Card {
            val whiteSpace = "\\s+".toPattern()
            val parts = input.substringAfter("Card").split(":", "|")
            val cardNumber = parts[0].trim().toInt()
            val winningNumbers = parts[1].trim().split(whiteSpace).map(String::toInt)
            val myNumbers = parts[2].trim().split(whiteSpace).map(String::toInt)
            return Card(cardNumber, winningNumbers, myNumbers)
        }
    }  
}
