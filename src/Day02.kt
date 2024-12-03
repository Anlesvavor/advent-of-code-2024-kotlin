import kotlin.math.absoluteValue

enum class IncrementType { DECEASING, INCREASING, NIL }

fun main() {
    fun parse(input: List<String>): List<List<Int>> {
        return input.map { it.split(" ").map(String::toInt) }
    }

    fun part1(input: List<String>): Int {
        fun safeRecord(record: List<Int>): Boolean {
            return record
                .zipWithNext()
                .map {
                    val diff = it.second.minus(it.first)
                    val incrementType = when {
                        diff > 0 -> IncrementType.INCREASING
                        diff < 0 -> IncrementType.DECEASING
                        else -> IncrementType.NIL
                    }
                    diff to incrementType
                }
                .headTail()
                .let { (head, tail) ->
                    if (head == null) { return@let false }
                    if (head.second == IncrementType.NIL) { return@let false }
                    tail.plus(head).all { (diff, incrementType) ->
                        diff.absoluteValue in IntRange(1, 3) && incrementType == head.second
                    }
                }
        }
        val parsed = parse(input)
        val m = parsed.map { it to safeRecord(it) }
        return m.count { it.second }
    }

    fun part2(input: List<String>): Int {
        fun <T> Iterable<T>.allWithDampener(predicate: (T?, T) -> Boolean): Boolean {
            var failedOnce = false
            var lastFailed: T? = null
            for (t in this) {
                val result = predicate(lastFailed, t)
                if (result == false) {
                    if (failedOnce) {
                        return false
                    } else {
                        failedOnce = true
                        lastFailed = t
                    }
                }
            }
            return true
        }
        fun deltas(record: List<Int>): List<Pair<Int, IncrementType>> {
            return record
                .zipWithNext()
                .map {
                    val diff = it.second.minus(it.first)
                    val incrementType = when {
                        diff > 0 -> IncrementType.INCREASING
                        diff < 0 -> IncrementType.DECEASING
                        else -> IncrementType.NIL
                    }
                    diff to incrementType
                }
        }
        fun safeRecord(record: List<Int>): Boolean {
            return record
                .zipWithNext()
                .map {
                    val diff = it.second.minus(it.first)
                    val incrementType = when {
                        diff > 0 -> IncrementType.INCREASING
                        diff < 0 -> IncrementType.DECEASING
                        else -> IncrementType.NIL
                    }
                    diff to incrementType
                }
                .headTail()
                .let { (head, tail) ->
                    if (head == null) { return@let false }
                    if (head.second == IncrementType.NIL) { return@let false }
                    tail.plus(head).reversed()
                        .allWithDampener { lastFailed, (diff, incrementType) ->
                        if (lastFailed != null) {
                            diff.minus(lastFailed.first).absoluteValue in IntRange(0, 3) && incrementType == head.second
                        } else {
                            diff.absoluteValue in IntRange(0, 3) && incrementType == head.second
                        }
                    }
                }
        }
        val parsed = parse(input)
        val m = parsed.map { Triple(it, deltas(it), safeRecord(it)) }
        return m.count { it.third }
    }

    // Notes
    /*
     0    1    3    2    4    5
       +1   +2   -1   +2   +1
        1    2       2 - -1  1 - -1
        1    2         3      2
     */

    // Read the input from the `src/Day02.txt` file.
    val input = readInput("Day02_test")
//    part1(input).println()
    part2(input).println()
}

