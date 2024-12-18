fun main() {
    fun part1(input: String): Int {
        fun splitIntoDigits(s: String): List<String> {
            val mid = s.length.div(2)
            return listOf(
                s.take(mid),
                s.drop(mid).trimStart('0').takeUnless { it == "" } ?: "0"
            )
        }
        fun blink(s: Sequence<String>): Sequence<String> {
            return s
                .flatMap {
                    when {
                        it == "0" -> listOf("1")
                        it.length.mod(2).equals(0) -> splitIntoDigits(it)
                        else -> listOf(it.toLong().times(2024).toString())
                    }
                }
        }
        val initial = input.split(" ").asSequence()
        var result: Sequence<String> = initial
        repeat(75) {
            result = blink(result)
        }
        return result.count()
    }

    "125 17".let {
        part1(it).println()
    }

    "1750884 193 866395 7 1158 31 35216 0".let {
        part1(it).println()
    }
}