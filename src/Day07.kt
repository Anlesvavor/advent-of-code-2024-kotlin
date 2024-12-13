
enum class Op { SUM, MUL }

fun <T> List<T>.takeTwoAndRest(): Triple<T, T, List<T>>? {
    return if(this.size >= 2) {
        Triple(this.first(), this.drop(1).first(), this.drop(2))
    } else {
        null
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        tailrec fun applyOperations(operands: List<List<Long>>): List<Long>  {
            if (operands.all { it.size <= 1 }) {
                return operands.flatten()
            }
            val op = operands.flatMap {
                if (it.size >= 2) {
                    val (first, second, rest) = it.takeTwoAndRest()!!
                    listOf(listOf(first.times(second)).plus(rest), listOf(first.plus(second)).plus(rest))
                } else { listOf(it) }
            }
            return applyOperations(op)
        }
        return input.map {
            val (result, operands) = it.split(':').let {
                assert(it.size == 2)
                it[0].toLong() to it[1].trim().split(' ').map { it -> it.toLong() }
            }
            applyOperations(listOf(operands)).firstOrNull { it == result } ?: 0L
        }.sum()
    }

    fun part2(input: List<String>): Long {
        tailrec fun applyOperations(operands: List<List<Long>>): List<Long>  {
            if (operands.all { it.size <= 1 }) {
                return operands.flatten()
            }
            val op = operands.flatMap {
                if (it.size >= 2) {
                    val (first, second, rest) = it.takeTwoAndRest()!!
                    listOf(
                        listOf(first.times(second)).plus(rest),
                        listOf(first.plus(second)).plus(rest),
                        listOf(first.toString().plus(second.toString()).toLong()).plus(rest)
                    )
                } else { listOf(it) }
            }
            return applyOperations(op)
        }
        return input.map {
            val (result, operands) = it.split(':').let {
                assert(it.size == 2)
                it[0].toLong() to it[1].trim().split(' ').map { it -> it.toLong() }
            }
            applyOperations(listOf(operands)).firstOrNull { it == result } ?: 0L
        }.sum()
    }

    readInput("Day07_test").let {
        part1(it).println()
        part2(it).println()
    }

    readInput("Day07").let {
        part1(it).println()
        part2(it).println()
    }

}