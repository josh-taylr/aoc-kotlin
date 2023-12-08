fun main() {
    val startNode = "AAA"
    val endNode = "ZZZ"

    fun part1(input: List<String>): Int {
        val instructions = input.first().asWrappingSequence().iterator()
        val nodes = input.drop(2).map(LRNode::parse)
            .associateBy { it.name }
        
        var node = startNode
        var steps = 0
        do {
            val map = instructions.next()
            node = when (map) {
                'L' -> nodes[node]!!.left
                'R' -> nodes[node]!!.right
                else -> error("")
            }
            steps += 1
        } while (node != endNode)

        return steps
    }

    fun part2(input: List<String>): Int {
        return Int.MAX_VALUE
    }

    check(part1(readInput("Day08_test")) == 6)

    val input = readInput("Day08")
    part1(input).println()
}

data class LRNode(val name: String, val left: String, val right: String) {

    companion object {
        fun parse(input: String): LRNode {
            val (name, left, right) =
                input.filter { c -> c.isLetter() || c == '=' || c == ',' }.split("=", ",")
            return LRNode(name, left, right)
        }
    }
}