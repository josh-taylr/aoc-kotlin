import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

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
                        val (destinationStart, sourceStart, rangeLen) = str.toUIntList()
                        val list = getOrPut(key) { SeedMapping() }
                        list.setRange(
                            source = (sourceStart..sourceStart + rangeLen),
                            destination = (destinationStart..destinationStart + rangeLen),
                        )
                    }
                }
            }

        return seeds.minOf { seed ->
            mappings.values.fold(seed) { value, mapping -> mapping[value] }
        }
    }

    fun part2(input: List<String>): UInt = runBlocking {
        val seedsInput = input.first()
        val mapsInput = input.drop(1)

        val seedRanges =
            seedsInput.substringAfter("seeds: ").toUIntList().windowed(size = 2, step = 2).map {
                (start, len) ->
                UIntRange(start, start + len)
            }
        val mappings =
            buildMap<String, SeedMapping> {
                var key = "NO_KEY"
                mapsInput.filter(String::isNotBlank).forEach { str ->
                    if (str.endsWith(" map:")) {
                        key = str.removeSuffix(" map:")
                    } else {
                        val (destinationStart, sourceStart, rangeLen) = str.toUIntList()
                        val list = getOrPut(key) { SeedMapping() }
                        list.setRange(
                            source = (sourceStart..sourceStart + rangeLen),
                            destination = (destinationStart..destinationStart + rangeLen),
                        )
                    }
                }
            }

        seedRanges
            .map { seeds ->
                async(Dispatchers.IO) {
                    seeds.minOf { seed ->
                        mappings.values.fold(seed) { value, mapping -> mapping[value] }
                    }
                }
            }
            .awaitAll()
            .min()
    }

    checkValue(35U, part1(readInput("Day05_test")))
    checkValue(46U, part2(readInput("Day05_test")))

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

class SeedMapping {
    private val list = mutableListOf(0U to 0U)

    operator fun get(index: UInt): UInt =
        list.run {
            val idx = binarySearchBy(index) { (source, _) -> source }
            val (source, destination) = if (idx >= 0) get(idx) else get(-(idx + 1) - 1)
            val offset = index - source
            return destination + offset
        }

    fun setRange(source: UIntRange, destination: UIntRange) =
        list.run {
            val idx = binarySearchBy(source.first) { (source, _) -> source }
            if (idx >= 0) {
                set(idx, source.first to destination.first)
                // set an end for this range, unless another range begins immediately
                if ((idx + 1) > lastIndex || get(idx + 1).first != source.last)
                    add(idx + 1, source.last to source.last)
            } else {
                add(-(idx + 1), source.first to destination.first)
                // set an end for this range, unless another range begins immediately
                if ((-(idx + 1) + 1) > lastIndex || get(-(idx + 1) + 1).first != source.last)
                    add(-(idx + 1) + 1, source.last to source.last)
            }
        }
}
