fun main() {
    fun part1(input: List<String>): UInt {
        val seedsInput = input.first()
        val mapsInput = input.drop(1)

        val seeds = seedsInput.substringAfter("seeds: ").toUIntList()
        val mappings =
            buildMap<String, SeedMapping> {
                var key = "NO_KEY"
                mapsInput.filter(String::isNotBlank).forEach { str ->
                    if (str.endsWith(" map:")) {
                        key = str.removeSuffix(" map:")
                    } else {
                        val (destStart, sourceStart, rangeLen) = str.toUIntList()
                        val list = getOrPut(key) { SeedMapping() }
                        list.setRange(
                                source = (sourceStart..sourceStart + rangeLen),
                                destination = (destStart..destStart + rangeLen),
                        )
                    }
                }
            }

        return seeds.minOf { seed ->
            mappings.values.fold(seed) { value, mapping -> mapping[value] }
        }
    }

    fun part2(input: List<String>): UInt {
        val seedsInput = input.first()
        val mapsInput = input.drop(1)

        val seedRanges = seedsInput.substringAfter("seeds: ").toUIntList()
                .windowed(size = 2, step = 2)
                .map { (start, len) -> UIntRange(start, start + len) }
        val mappings =
                buildMap<String, SeedMapping> {
                    var key = "NO_KEY"
                    mapsInput.filter(String::isNotBlank).forEach { str ->
                        if (str.endsWith(" map:")) {
                            key = str.removeSuffix(" map:")
                        } else {
                            val (destStart, sourceStart, rangeLen) = str.toUIntList()
                            val list = getOrPut(key) { SeedMapping() }
                            list.setRange(
                                    source = (sourceStart..sourceStart + rangeLen),
                                    destination = (destStart..destStart + rangeLen),
                            )
                        }
                    }
                }

        return seedRanges.minOf { seedRange ->
            seedRange.minOf { seed ->
                mappings.values.fold(seed) { value, mapping -> mapping[value] }
            }
        }
    }

    checkValue(35U, part1(readInput("Day05_test")))
    checkValue(46U, part2(readInput("Day05_test")))

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

class SeedMapping {
    private var list = mutableListOf(
            0U to 0U,
            UInt.MAX_VALUE to UInt.MAX_VALUE
    )

    operator fun get(index: UInt): UInt {
        val idx = list.binarySearchBy(index) { (source, _) -> source }
        val (source, destination) = if (idx >= 0) list[idx] else list[insertionPoint(idx) - 1]
        val offset = index - source
        return destination + offset
    }

    fun setRange(source: UIntRange, destination: UIntRange) {
        list = list.apply {
            val idx = binarySearchBy(source.first) { (source, _) -> source }
            if (idx >= 0) {
                set(idx, source.first to destination.first)
                // set an end for this range, unless a another range begins immediately
                if (get(idx + 1).first != source.last)
                    add(idx + 1, source.last to source.last)
            } else {
                add(insertionPoint(idx), source.first to destination.first)
                // set an end for this range, unless a another range begins immediately
                if (get(insertionPoint(idx) + 1).first != source.last)
                    add(insertionPoint(idx) + 1, source.last to source.last)
            }
        }
    }

    private fun insertionPoint(idx: Int) = -(idx + 1)
}
