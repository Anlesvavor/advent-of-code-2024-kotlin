
sealed class Instruction {
    class DoStatement : Instruction()
    class DontStatement : Instruction()
    class MultResult(val result: Int) : Instruction()

    companion object {
        fun parse(s: String): Instruction? = when {
            s.startsWith("do()") -> DoStatement()
            s.startsWith("don't()") -> DontStatement()
            else -> parseMult(s)?.let(::MultResult)
        }
    }
}

fun parseMult(s: String): Int? {
    val prefix = "mul("
    if (!s.startsWith(prefix)) {
        return null
    }
    val n = s.drop(prefix.length).takeWhile(Char::isDigit).take(3)
    if (n.length !in IntRange(1, 3)) {
        return null
    }
    if (s.drop(prefix.length + n.length).take(1) != ",") {
        return null
    }
    val m = s.drop(prefix.length + n.length + 1).takeWhile(Char::isDigit).take(3)
    if (m.length !in IntRange(1, 3)) {
        return null
    }
    if (s.drop(prefix.length + n.length + 1 + m.length).take(1) != ")") {
        return null
    }
    return m.toInt() * n.toInt()
}

fun main() {

    fun part1(input: List<String>): Int {
        fun parse(s: String): Int {
            return s
                .windowed(size = "mul(xxx,yyy)".length, step = 1, partialWindows = true)
                .mapNotNull(::parseMult)
                .sum()
        }
        return input.sumOf(::parse)
    }

    fun part2(input: List<String>): Int {
        fun parse(s: String): List<Instruction> {
            // using the same window size just because the "do" and "don't" statements are shorter
            return s
                .windowed(size = "mul(xxx,yyy)".length, step = 1, partialWindows = true)
                .mapNotNull(Instruction::parse)
        }
        return input
            .reduce(String::plus)
            .let(::parse)
            .fold((true to 0)) { (enabled, sum), i ->
                when (i) {
                    is Instruction.DoStatement -> true to sum
                    is Instruction.DontStatement -> false to sum
                    is Instruction.MultResult -> enabled to if (enabled) sum.plus(i.result) else sum
                }
            }.second
    }

    readInput("Day03_test").let {
        part1(it).println()
        part2(it).println()
        println()
    }

    readInput("Day03").let {
        part1(it).println()
        part2(it).println()
        println()
    }
}