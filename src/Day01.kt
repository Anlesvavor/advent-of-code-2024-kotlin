import kotlin.math.abs

fun main() {
    fun parseLocationLists(input: List<String>): Pair<List<Int>, List<Int>> {
        return input
            .map {
                val p = it.split("   ")
                p[0].toInt() to p[1].toInt()
            }
            .unzip()
    }

    fun part1(input: List<String>): Int {
        val (firstList, secondList) = parseLocationLists(input)
        return firstList
            .sorted()
            .zip(secondList.sorted())
            .sumOf { abs(it.first - it.second) }
    }

    fun part2(input: List<String>): Int {
        val (firstList, secondList) = parseLocationLists(input)
        val countsOnSecondList = secondList.groupBy { it }.mapValues { it.value.count() }
        return firstList.sumOf {
            countsOnSecondList.getOrDefault(it, 0).times(it)
        }
    }


    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
