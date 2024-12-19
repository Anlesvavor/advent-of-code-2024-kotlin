
tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a.mod(b))

data class Rational(val numerator: Long, val denominator: Long) {

    fun simplify(): Rational {
        val g = gcd(numerator, denominator)
        return Rational(numerator.div(g), denominator.div(g))
    }

    operator fun plus(other: Rational): Rational {
        return Rational(
            numerator = numerator * other.denominator + other.numerator * denominator,
            denominator = denominator * other.denominator
        ).simplify()
    }

    operator fun unaryMinus(): Rational {
        return Rational(-numerator, denominator)
    }

    operator fun minus(other: Rational): Rational {
        return this + (-other)
    }

    operator fun div(other: Rational): Rational {
        return Rational(numerator * other.denominator, denominator * other.numerator).simplify()
    }

    operator fun times(other: Rational): Rational {
        return Rational(numerator * other.numerator, denominator * other.denominator).simplify()
    }

    fun toFloat() = numerator.toFloat().div(denominator.toFloat())

    fun toIntOrNull(): Int? = if (isWhole()) numerator.div(denominator).toInt() else null

    fun toLongOrNull(): Long? = if (isWhole()) numerator.div(denominator) else null

    fun isWhole() = numerator.rem(denominator) == 0L
}

fun Long.minus(other: Rational): Rational {
    return Rational(this, 1L) - other
}

fun Long.times(other: Rational): Rational {
    return Rational(this, 1L) * other
}

data class System(val ax: Long, val ay: Long, val bx: Long, val by: Long, var px: Long, var py: Long) {
    fun solve(): Pair<Rational, Rational> {
        val y =
            py.minus(bx.times(Rational(px, ax))) /
                    by.minus(bx.times(Rational(ay, ax)))
        val x = Rational(px, ax).minus(Rational(ay, ax).times(y))
        return x to y
    }
}

fun main() {
    System(94L, 22L, 34L, 67L, 8400L, 5400L)
        .solve()
        .let { it.first.toFloat() to it.second.toFloat() }
        .println()

    fun parseSystem(paragraph: List<String>): System {
        fun getNumbers(s: String): Pair<Long, Long> {
            val numbers = Regex("[0-9]+")
                .findAll(s)
                .map(MatchResult::value)
                .toList()
            assert(numbers.size == 2)
            return numbers[0].toLong() to numbers[1].toLong()
        }
        assert(paragraph.size == 3)
        val (ax, bx) = getNumbers(paragraph[0])
        val (ay, by) = getNumbers(paragraph[1])
        val (px, py) = getNumbers(paragraph[2])
        return System(ax, ay, bx, by, px, py)
    }


    fun part1(input: List<String>): Int {
        return input
            .filter(String::isNotBlank)
            .chunked(3, ::parseSystem)
            .mapNotNull {
                try {
                    it.solve().let { (buttonA, buttonB) ->
                        val costA = buttonA.toIntOrNull()?.times(3) ?: return@mapNotNull null
                        val costB = buttonB.toIntOrNull() ?: return@mapNotNull null
                        costA + costB
                    }
                } catch(_ : Exception) {
                    null
                }
            }
            .sum()
    }

    fun part2(input: List<String>): Long {
        return input
            .filter(String::isNotBlank)
            .chunked(3) {
                parseSystem(it).apply {
                    px = px + 10000000000000L
                    py = py + 10000000000000L
                }
            }
            .mapNotNull {
                tryOrNull {
                    it.solve().let { (buttonA, buttonB) ->
                        val costA = buttonA.toLongOrNull()?.times(3) ?: return@mapNotNull null
                        val costB = buttonB.toLongOrNull() ?: return@mapNotNull null
                        costA + costB
                    }
                }
            }
            .sum()
    }

    readInput("Day13_test").let {
        part1(it).println()
        part2(it).println()
    }

    readInput("Day13").let {
        part1(it).println()
        part2(it).println()
    }

}