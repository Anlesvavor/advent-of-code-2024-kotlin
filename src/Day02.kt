import kotlin.math.absoluteValue

enum class IncrementType { DECEASING, INCREASING, NIL }
// enum class BoolPlus { TRUE, FALSE, AMENDED }

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
                    if (head == null) {
                        return@let false
                    }
                    if (head.second == IncrementType.NIL) {
                        return@let false
                    }
                    tail.plus(head).all { (diff, incrementType) ->
                        diff.absoluteValue in IntRange(1, 3) && incrementType == head.second
                    }
                }
        }

        val parsed = parse(input)
        val m = parsed.map { it to safeRecord(it) }
        return m.count { it.second }
    }

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

    /**
     * Leaving these commented code for posterity
     */
    //    fun part2(input: List<String>): Int {
//        fun getIncrementType(diff: Int?): IncrementType = when {
//            diff == null -> IncrementType.NIL
//            diff > 0 -> IncrementType.INCREASING
//            diff < 0 -> IncrementType.DECEASING
//            else -> IncrementType.NIL
//        }
//        fun safeRecord(record: List<Int>): Any {
//            val rPrime = record
//                .windowed(size = 3, step = 1, partialWindows = true)
//                .filter { it.size >= 2 }
//                .map { it: List<Int> ->
//                    check(it.size >= 2)
//                    val immediateDiff = it[1].minus(it[0])
//                    var skippingOneDiff: Int? = null
//                    if (it.size == 3) {
//                        skippingOneDiff = it[2].minus(it[0])
//                    }
//                    (immediateDiff to getIncrementType(immediateDiff)) to (skippingOneDiff to getIncrementType(skippingOneDiff))
//                }
//            var failedOnce = false
//            val firstIncrementType = rPrime.first().first.second
//            for (i in rPrime) {
//                if (i.first.first.absoluteValue in IntRange(1, 3) && i.first.second == firstIncrementType) {
//                    continue
//                } else {
//                    if (failedOnce) {
//                        return BoolPlus.FALSE
//                    } else {
//                        // This happens when the list reached the end; the one to skip is the last item.
//                        if (i.second.first == null) {
//                            return BoolPlus.AMENDED
//                        }
//                        if (i.second.first?.absoluteValue in IntRange(1, 3) && i.second.second == firstIncrementType) {
//                            failedOnce = true
//                            continue
//                        } else {
//                            return BoolPlus.FALSE
//                        }
//                    }
//                }
//            }
//            if (failedOnce) {
//               return BoolPlus.AMENDED
//            } else {
//                return BoolPlus.TRUE
//            }
//        }
//        val parsed = parse(input)
//        val m = parsed.map { Pair(it, safeRecord(it) to safeRecord(it.drop(1))) }
//        val n =  m.map {
//            val r = when {
//                it.second.first == BoolPlus.TRUE -> true
//                it.second.first == BoolPlus.AMENDED -> true
//                it.second.first == BoolPlus.FALSE && it.second.second == BoolPlus.TRUE -> true
//                else -> false
//            }
//            Triple(it.first.zipWithNext { a, b -> b-a}, it.second, r)
//
//        }
//        return n.count { it.third }
//    }
//    fun part2(input: List<String>): Int {
//        val parsed = parse(input)
//        val m = parsed
//            .map { record ->
//                val levelDiffs = record.zipWithNext { a, b -> b - a }
//                val groups = levelDiffs.groupBy { it.compareTo(0) }
//                val incrementingGroup = groups.get(1)
//                val decrementingGroup = groups.get(-1)
//                when {
//                    decrementingGroup == null && incrementingGroup != null -> {
//                        incrementingGroup
//                            .filter { it.absoluteValue in IntRange(1, 3) }
//                            .count() >= levelDiffs.size.dec()
//                    }
//                    incrementingGroup == null && decrementingGroup != null -> {
//                        decrementingGroup
//                            .filter { it.absoluteValue in IntRange(1, 3) }
//                            .count() >= levelDiffs.size.dec()
//                    }
//                    else -> false
//                }
//            }
//            .count { it }
//        return m
//    }
    fun part2(input: List<String>): Int {
        fun safeRecord(record: List<Int>): Boolean {
                val levelDiffs = record.zipWithNext { a, b -> b - a }
                val groups = levelDiffs.groupBy { it.compareTo(0) }
                val incrementingGroup = groups.get(1)
                val decrementingGroup = groups.get(-1)
                return when {
                    decrementingGroup == null && incrementingGroup != null -> {
                        incrementingGroup
                            .filter { it.absoluteValue in IntRange(1, 3) }
                            .count() >= levelDiffs.size
                    }
                    incrementingGroup == null && decrementingGroup != null -> {
                        decrementingGroup
                            .filter { it.absoluteValue in IntRange(1, 3) }
                            .count() >= levelDiffs.size
                    }
                    else -> false
                }
        }
        val parsed = parse(input)
        val m = parsed
            .map {
                var copies = mutableListOf<List<Int>>()
                it.indices.forEach { i ->
                    it.filterIndexed { j, _ -> j != i }
                        .let { copies.add(it) }
                }
                it to copies.toList()
            }
        val n = m.map { (original, copies) ->
            if (safeRecord(original)) {
                true
            } else {
                copies.any { safeRecord(it) }
            }
        }
        return n.count { it }
    }

    // Notes
    /*
     0    1    3    2    4    5
       +1   +2   -1   +2   +1
        1    2       2 - -1  1 - -1
        1    2         3      2
     */

    // Read the input from the `src/Day02.txt` file.
    readInput("Day02_test").let {
        part1(it).println()
        part2(it).println()
    }
    println("---")
    readInput("Day02").let {
        part1(it).println()
        part2(it).println()
    }
}

