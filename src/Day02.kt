fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val game = Game.parse(line)
            if (game.isPosible()) game.id else 0
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val game = Game.parse(line)

            val minCount =
                game.cubeSets
                    .flatMap(Set<Cubes>::toList)
                    .groupBy { cubes -> cubes::class }
                    .mapValues { (_, cubes) -> cubes.maxOf { it.count } }

            val minBlues = minCount[Cubes.Blue::class] ?: 0
            val minReds = minCount[Cubes.Red::class] ?: 0
            val minGreens = minCount[Cubes.Green::class] ?: 0

            minBlues * minReds * minGreens
        }
    }

    check(part1(readInput("Day02_test")) == 8)
    check(part2(readInput("Day02_test2")) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

class Game(val id: Int, val cubeSets: List<Set<Cubes>>) {
    fun isPosible(reds: Int = 12, greens: Int = 13, blues: Int = 14): Boolean {
        return cubeSets.all { set ->
            set.all { cubes ->
                when (cubes) {
                    is Cubes.Blue -> cubes.count <= blues
                    is Cubes.Green -> cubes.count <= greens
                    is Cubes.Red -> cubes.count <= reds
                }
            }
        }
    }

    companion object {
        fun parse(str: String): Game {
            val parts = str.split(":")
            val id = parts[0].filter(Char::isDigit).toInt()
            val cubeSetParts = parts[1].split(";")
            val cubeSets = mutableListOf<Set<Cubes>>()

            for (part in cubeSetParts) {
                val cubeSet =
                    part
                        .split(",")
                        .map { cubeSetPart ->
                            val cubeData = cubeSetPart.trim().split(" ")
                            val quantity = cubeData[0].toInt()
                            when (val color = cubeData[1]) {
                                "blue" -> Cubes.Blue(quantity)
                                "red" -> Cubes.Red(quantity)
                                "green" -> Cubes.Green(quantity)
                                else -> throw IllegalArgumentException("Unknown cube color: $color")
                            }
                        }
                        .toSet()
                cubeSets.add(cubeSet)
            }

            return Game(id, cubeSets)
        }
    }
}

sealed class Cubes(val count: Int) {
    class Blue(count: Int) : Cubes(count)

    class Red(count: Int) : Cubes(count)

    class Green(count: Int) : Cubes(count)
}
