fun main() {
    fun part1(input: List<String>): Int {
        val sortedHands = input.map(Hand::parse).sorted()
        val ranks = 1..sortedHands.count()
        return sortedHands.zip(ranks).sumOf { (hand, rank) -> hand.bid * rank }
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    check(part1(readInput("Day07_test")) == 6440)

    val input = readInput("Day07")
    part1(input).println()
}

data class Hand(val cards: String, val bid: Int) : Comparable<Hand> {

    val type = Type.parse(cards)

    override fun compareTo(other: Hand): Int =
        type.compareTo(other.type).takeIf { it != 0 } ?: secondOrderingRule(other)

    override fun toString() = "Hand(cards=$cards, bid=$bid, type=${type.name})"

    private fun secondOrderingRule(other: Hand): Int =
        this.cards
            .asSequence() // an unnecessarily optimisation, or logical execusion    
            .zip(other.cards.asSequence())
            .map { (a, b) -> labelOrder.indexOf(a).compareTo(labelOrder.indexOf(b)) }
            .first { it != 0 }

    companion object {
        val labelOrder =
            listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2').reversed()

        fun parse(input: String): Hand {
            val (cards, bid) = input.split(" ")
            return Hand(cards, bid.toInt())
        }
    }

    enum class Type {
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
                cards.groupingBy { it }.eachCount().values.sortedDescending().filterNot { it < 2 }
        }
    }
}
