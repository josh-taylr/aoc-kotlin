fun main() {
    fun part1(input: List<String>): Int {
        val sortedHands = input.map(Hand::parse).sorted()
        val ranks = 1..sortedHands.count()
        return sortedHands.zip(ranks).sumOf { (hand, rank) -> hand.bid * rank }
    }

    fun part2(input: List<String>): Int {
        val sortedHands = input.map(Hand::parse).sortedWith(Hand.JokerHandComparator)
        val ranks = 1..sortedHands.count()
        return sortedHands.zip(ranks).sumOf { (hand, rank) -> hand.bid * rank }
    }

    checkValue(6440, part1(readInput("Day07_test")))
    checkValue(5905, part2(readInput("Day07_test")))

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

data class Hand(val cards: String, val bid: Int) : Comparable<Hand> {

    private val type = Type.parse(cards)

    override fun compareTo(other: Hand) = DefaultHandComparator.compare(this, other)

    override fun toString() = "Hand(cards=$cards, bid=$bid, type=${type.name})"

    companion object {
        fun parse(input: String): Hand {
            val (cards, bid) = input.split(" ")
            return Hand(cards, bid.toInt())
        }

        val DefaultHandComparator =
            object : Comparator<Hand> {

                val labelOrder =
                    listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')

                override fun compare(a: Hand, b: Hand) =
                    a.type.compareTo(b.type).takeIf { it != 0 }
                        ?: SecondOrderingRule(labelOrder).compare(a, b)
            }

        val JokerHandComparator =
            object : Comparator<Hand> {

                val labelOrder =
                    listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')

                override fun compare(a: Hand, b: Hand) =
                    bestHand(a).type.compareTo(bestHand(b).type).takeIf { it != 0 }
                        ?: SecondOrderingRule(labelOrder).compare(a, b)

                fun bestHand(hand: Hand): Hand =
                    hand.run {
                        val labelCounts = cards.groupingBy { it }.eachCount()
                        val commonCard =
                            labelCounts.filterKeys { it != 'J' }.maxByOrNull { it.value }
                        if (commonCard == null) {
                            Hand(
                                cards.replace('J', labelOrder.last()),
                                bid
                            ) // cards in hand are all jokers
                        } else {
                            Hand(cards.replace('J', commonCard.key), bid)
                        }
                    }
            }
    }

    private enum class Type {
        HighCard,
        OnePair,
        TwoPair,
        ThreeOfAKind,
        FullHouse,
        FourOfAKind,
        FiveOfAKind;

        companion object {
            fun parse(str: String) =
                when (sameLabelCounts(str)) {
                    listOf(5) -> FiveOfAKind
                    listOf(4) -> FourOfAKind
                    listOf(3, 2) -> FullHouse
                    listOf(3) -> ThreeOfAKind
                    listOf(2, 2) -> TwoPair
                    listOf(2) -> OnePair
                    else -> HighCard
                }

            private fun sameLabelCounts(cards: String): List<Int> =
                cards
                    .groupingBy { it }
                    .eachCount()
                    .filterValues { it >= 2 }
                    .values
                    .sortedDescending()
        }
    }

    private class SecondOrderingRule(labelOrder: List<Char>) : Comparator<Hand> {

        private val cardComparator = compareBy<Char> { labelOrder.indexOf(it) }

        override fun compare(a: Hand, b: Hand) =
            a.cards
                .asSequence() // an unnecessary optimisation, or logical code execution
                .zip(b.cards.asSequence())
                .map { (a, b) -> cardComparator.compare(a, b) }
                .firstOrNull { comparison -> comparison != 0 } ?: 0
    }
}
