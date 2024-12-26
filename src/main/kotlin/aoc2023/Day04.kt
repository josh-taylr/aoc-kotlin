package aoc2023

import AOCDay

class Day04 : AOCDay(year = "2023", day = "4")  {
    override fun part1(input: List<String>): Int {
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

    override fun part2(input: List<String>): Int {
        val cards = input.map(Card.Companion::parse).associateBy { it.cardNumber }
        val deque = Pile(cards.values)
        var pileCount = cards.count()
        while (deque.isNotEmpty()) {
            val card = deque.removeFirst()
            val count = card.matchingNumbers.count()
            for (i in card.cardNumber + 1..card.cardNumber + count) {
                val nextCard = cards[i] ?: continue
                deque.addLast(nextCard)
                pileCount++
            }
        }
        return pileCount
    }
}

data class Card(val cardNumber: Int, val winningNumbers: List<Int>, val myNumbers: List<Int>) {
    val matchingNumbers: List<Int> by lazy { myNumbers.filter { num -> num in winningNumbers } }

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

typealias Pile = ArrayDeque<Card>
