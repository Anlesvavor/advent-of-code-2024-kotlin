


operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> {
    return first.minus(other.first) to second.minus(other.second)
}

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return first.plus(other.first) to second.plus(other.second)
}

fun Pair<Int, Int>.deltaFirst(other: Pair<Int, Int>) = other.first - first
fun Pair<Int, Int>.deltaSecond(other: Pair<Int, Int>) = other.second - second

data class Antenna(val id: Char, val row: Int, val col: Int) {
    val coord: Pair<Int, Int>
        get() = row to col

    fun antinodeWith(other: Antenna): Pair<Int, Int> {
        return other.coord.plus(coord.deltaFirst(other.coord) to coord.deltaSecond(other.coord))
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val (height, width) = input.let {
            it.size to it.first().length
        }
        return input
            .flatMapIndexed { r, row ->
                row.mapIndexedNotNull { c, char ->
                    if (char != '.') {
                        Antenna(char, r, c)
                    } else null
                }
            }
            .groupBy { it.id }
            .mapValues {
                it.value.flatMap { cur -> it.value.map { o -> cur.antinodeWith(o) }.filter { it != cur.coord } }
            }
            .values
            .flatten()
            .filter { it.first in 0..<height && it.second in 0..<width }
            .distinct()
            .count()
    }

    readInput("Day08_test").let {
        part1(it).println()
    }

    readInput("Day08").let {
        part1(it).println()
    }
}