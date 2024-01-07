fun main() {
    fun part1(input: List<String>): Int {
        val instructions = input.first()
        val nodes = input.drop(2).map(LRNode::parse).associateBy { it.name }
        return countSteps(instructions, nodes, "AAA") { it == "ZZZ" }
    }

    fun part2(input: List<String>): Long {
        val instructions = input.first()
        val nodeMap = input.drop(2).map(LRNode::parse).associateBy { it.name }
        val startKeys = nodeMap.filterKeys { it.endsWith('A') }.map { it.key }
        val steps =
            startKeys.map { key ->
                countSteps(instructions, nodeMap, key) { it.endsWith('Z') }.toLong()
            }
        return steps.reduce { lcm, n -> lcm(lcm, n) }
    }

    checkValue(2, part1(readInput("Day08_test")))
    checkValue(6, part2(readInput("Day08_test2")))

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}

private inline fun countSteps(
    instructions: CharSequence,
    map: Map<String, LRNode>,
    startKey: String,
    isComplete: (key: String) -> Boolean,
): Int {
    val iterator = instructions.wrappingSequence().iterator()
    var currentKey = startKey
    var steps = 0
    do {
        currentKey =
            when (iterator.next()) {
                'L' -> map.getValue(currentKey).left
                'R' -> map.getValue(currentKey).right
                else -> error("Invalid instruction")
            }
        steps += 1
    } while (!isComplete(currentKey))
    return steps
}

private data class LRNode(val name: String, val left: String, val right: String) {

    companion object {
        fun parse(input: String): LRNode {
            val (name, left, right) =
                input.filter { c -> c.isLetterOrDigit() || c == '=' || c == ',' }.split("=", ",")
            return LRNode(name, left, right)
        }
    }
}
