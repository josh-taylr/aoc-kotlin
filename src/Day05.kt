fun main() {
    fun part1(input: List<String>): Long {
        val seedsInput = input.first()
        val mapsInput = input.drop(1)
        
        val seeds = seedsInput.substringAfter("seeds: ").toLongList()
        val mappings = buildMap<String, InfiniteList> {
            var key = "NO_KEY"
            mapsInput.filter(String::isNotBlank)
                .forEach { str ->
                    if (str.endsWith(" map:")) {
                        key = str.removeSuffix(" map:")
                    } else {
                        val (destStart, sourceStart, rangeLen) = str.toLongList()
                        val list = getOrPut(key) { InfiniteList() }
                        list.setRange(
                            sourceRange = (sourceStart..sourceStart + rangeLen),
                            destRange = (destStart..destStart + rangeLen),
                        )
                    }
                }
        }
        
        return seeds.minOf { seed ->
            mappings.values.fold(seed) { value, mapping ->
                mapping[value]
            }
        }
    }

    fun part2(input: List<String>): Long {
        return Long.MAX_VALUE
    }

    check(part1(readInput("Day05_test")) == 35L)

    val input = readInput("Day05")
    part1(input).println()
//    part2(input).prLongln()
}

class InfiniteList {
    private val map = mutableMapOf<LongRange, LongRange>()
    
    operator fun get(index: Long): Long {
        for ((source, dest) in map) {
            if (index in source) {
                val offest = index - source.start
                return dest.start + offest
            }
        }
        return index
    }
    
    fun setRange(sourceRange: LongRange, destRange: LongRange) {
        map[sourceRange] = destRange
    }
}
